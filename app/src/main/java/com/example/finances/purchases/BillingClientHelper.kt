import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*

class BillingClientHelper(context: Context) : PurchasesUpdatedListener {

    interface OnQueryProductsListener {
        fun onSuccess(products: List<SkuDetails>)
        fun onFailure(error: Error)
    }

    interface OnPurchaseListener {
        fun onPurchaseSuccess(purchase: Purchase?)
        fun onPurchaseFailure(error: Error)
    }

    interface OnQueryActivePurchasesListener {
        fun onSuccess(activePurchases: List<Purchase>)
        fun onFailure(error: Error)
    }

    interface OnQueryPurchaseHistoryListener {
        fun onSuccess(purchaseHistoryList: List<PurchaseHistoryRecord>)
        fun onFailure(error: Error)
    }

    var onPurchaseListener: OnPurchaseListener? = null

    class Error(val responseCode: Int, val debugMessage: String)

    private val billingClient = BillingClient
        .newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    fun purchase(activity: Activity, product: SkuDetails) {
        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(
                    activity,
                    BillingFlowParams.newBuilder().setSkuDetails(product).build()
                )
            }
        }
    }

    fun queryProducts(listener: OnQueryProductsListener) {
        val skusList = listOf("rsstudy.month")

        queryProductsForType(
            skusList,
            BillingClient.SkuType.SUBS
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = skuDetailsList ?: mutableListOf()
                queryProductsForType(
                    skusList,
                    BillingClient.SkuType.INAPP
                ) { billingResult, skuDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        products.addAll(skuDetailsList ?: listOf())
                        listener.onSuccess(products)
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private fun queryProductsForType(
        skusList: List<String>,
        @BillingClient.SkuType type: String,
        listener: SkuDetailsResponseListener
    ) {
        onConnected {
            billingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder().setSkusList(skusList).setType(type).build(),
                listener
            )
        }
    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                block()
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchaseList: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchaseList == null) {
                    //to be discussed in the next article
                    onPurchaseListener?.onPurchaseSuccess(null)
                    return
                }

                purchaseList.forEach(::processPurchase) //to be declared below
            }
            else -> {
                //error occurred or user canceled
                onPurchaseListener?.onPurchaseFailure(
                    BillingClientHelper.Error(
                        billingResult.responseCode,
                        billingResult.debugMessage
                    )
                )
            }
        }
    }

    private fun acknowledgePurchase(
        purchase: Purchase,
        callback: AcknowledgePurchaseResponseListener
    ) {
        onConnected {
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                    .build(),
                callback::onAcknowledgePurchaseResponse
            )
        }
    }

    private fun consumePurchase(purchase: Purchase, callback: ConsumeResponseListener) {
        onConnected {
            billingClient.consumeAsync(
                ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            ) { billingResult, purchaseToken ->
                callback.onConsumeResponse(billingResult, purchaseToken)
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            onPurchaseListener?.onPurchaseSuccess(purchase)

            if (purchase.skus.firstOrNull() == "coin_pack_large") {
                //consuming our only consumable product
                consumePurchase(purchase) { billingResult, purchaseToken ->
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        //implement retry logic or try to consume again in onResume()
                    }
                }
            } else if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase) { billingResult ->
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        //implement retry logic or try to acknowledge again in onResume()
                    }
                }
            }
        }
    }

    private fun queryActivePurchasesForType(
        @BillingClient.SkuType type: String,
        listener: PurchasesResponseListener
    ) {
        onConnected {
            billingClient.queryPurchasesAsync(type, listener)
        }
    }

    fun queryActivePurchases(listener: OnQueryActivePurchasesListener) {
        queryActivePurchasesForType(
            BillingClient.SkuType.SUBS
        ) { billingResult, activeSubsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                queryActivePurchasesForType(
                    BillingClient.SkuType.INAPP
                ) { billingResult, nonConsumableProductsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        listener.onSuccess(
                            activeSubsList.apply { addAll(nonConsumableProductsList) }
                        )
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    private fun queryPurchasesHistoryForType(
        @BillingClient.SkuType type: String,
        listener: PurchaseHistoryResponseListener
    ) {
        onConnected {
            billingClient.queryPurchaseHistoryAsync(type, listener)
        }
    }

    fun queryPurchaseHistory(listener: OnQueryPurchaseHistoryListener) {
        queryPurchasesHistoryForType(
            BillingClient.SkuType.SUBS
        ) { billingResult, subsHistoryList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                queryPurchasesHistoryForType(
                    BillingClient.SkuType.INAPP
                ) { billingResult, inappHistoryList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        listener.onSuccess(
                            subsHistoryList?.apply { addAll(inappHistoryList.orEmpty()) }.orEmpty()
                        )
                    } else {
                        listener.onFailure(
                            Error(billingResult.responseCode, billingResult.debugMessage)
                        )
                    }
                }
            } else {
                listener.onFailure(
                    Error(billingResult.responseCode, billingResult.debugMessage)
                )
            }
        }
    }

    fun querySyncedActivePurchases(listener: OnQueryActivePurchasesListener) {
        queryPurchaseHistory(object : OnQueryPurchaseHistoryListener {
            override fun onSuccess(purchaseHistoryList: List<PurchaseHistoryRecord>) {
                queryActivePurchases(listener)
            }

            override fun onFailure(error: Error) {
                listener.onFailure(error)
            }
        })
    }

    fun changeSubscription(
        activity: Activity,
        newSub: SkuDetails,
        updateParams: BillingFlowParams.SubscriptionUpdateParams
    ) {
        onConnected {
            activity.runOnUiThread {
                billingClient.launchBillingFlow(
                    activity,
                    BillingFlowParams.newBuilder().setSkuDetails(newSub)
                        .setSubscriptionUpdateParams(updateParams).build()
                )
            }
        }
    }
}