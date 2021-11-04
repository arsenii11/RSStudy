package com.example.finances.purchases

import BillingClientHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.adapty.Adapty
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.example.finances.R
import javax.inject.Inject

class SubscribeActivity : AppCompatActivity() { /*BillingClientHelper.OnPurchaseListener*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        Adapty.getPaywalls { paywalls, products, error ->
            if (error == null) {
                var button = findViewById<Button>(R.id.subscribe_btn)
                button.text = paywalls?.get(0)?.products?.get(0)?.localizedTitle
            }
        }
    }


    /*
    @Inject
    lateinit var billingClientHelper: BillingClientHelper

    private val purchaseButtonsMap: Map<String, Button> by lazy(LazyThreadSafetyMode.NONE) {
        mapOf(
            "rsstudy.month" to findViewById(R.id.subscribe)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        billingClientHelper.onPurchaseListener = this

        displayProducts()
    }

    private fun displayProducts() {
        billingClientHelper.queryProducts(object : BillingClientHelper.OnQueryProductsListener {
            override fun onSuccess(products: List<SkuDetails>) {
                products.forEach { product ->
                    purchaseButtonsMap[product.sku]?.apply {
                        text = "${product.description} for ${product.price}"
                        setOnClickListener {
                            billingClientHelper.purchase(this@SubscribeActivity, product) //will be declared below
                        }
                    }
                }
            }

            override fun onFailure(error: BillingClientHelper.Error) {
                //handle error
            }
        })
    }

    override fun onPurchaseSuccess(purchase: Purchase?) {
        TODO("Not yet implemented")
    }

    override fun onPurchaseFailure(error: BillingClientHelper.Error) {
        TODO("Not yet implemented")
    }*/
}
