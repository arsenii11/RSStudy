package com.example.finances.purchases;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BillingClientHelper {

    public final static String SUBSCRIPTION_MONTH = "rsstudy.month";

    private Context context; //Контекст приложения
    private BillingClient billingClient; //Платежный клиент
    private List<Purchase> purchasesSub; //Список подписок пользователя

    //Конструктор для помощника
    public BillingClientHelper (Context context){
        this.context = context; //Присваиваем контекст
        purchasesSub = new ArrayList<>(); //Инициализация списка
        create(); //Создаем клиент
        startConnection(); //Запускаем соединение
    }

    //Функция создания нового платежного клиента
    private void create(){
        //Создаем новый платежный клиент
        billingClient = BillingClient.newBuilder(context).
                setListener((billingResult, list) ->
                    getPurchases()).
                enablePendingPurchases().
                build();
    }

    //Функция подключения к Google Play
    private void startConnection(){

        //Запускаем соединение с Google Play
        billingClient.startConnection(new BillingClientStateListener() {

            //Соединение не установлено
            @Override
            public void onBillingServiceDisconnected() {
                billingClient.startConnection(this); //пробуем еще раз подключиться
            }

            //Соединение установлено
            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                if (isOK(billingResult)) {

                    //Проверяем, поддерживает ли устройство пользователя подписки
                    if (isOK(billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)))
                        getPurchases(); //Получаем список подписок пользоваетеля
                }
            }
        });
    }

    //Функция проверки на ответ OK
    private boolean isOK(BillingResult billingResult){
        return billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }

    //Функция для получения всех покупок пользователя
    private void getPurchases(){
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (queryBillingResult, list) -> {
            if(isOK(queryBillingResult)) {
                List<Purchase> local = list;
                purchasesSub.addAll(local);
            }
        });
    }

    //Функция проверки наличия подписки у пользователя
    public boolean isSub(String subscriptionName){
        return purchasesSub.stream().filter(p -> p.getSkus().contains(subscriptionName)).findAny().orElse(null) != null;
    }

    public void subscribe(Activity activity, String subscriptionName){

        List<String> skuList = new ArrayList<> ();
        skuList.add(subscriptionName);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, list) -> {
                    Log.e("SIZE", String.valueOf(list.size()));
                    if(isOK(billingResult)) {
                        SkuDetails skuDetails = list.get(0);

                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();

                        billingClient.launchBillingFlow(activity, billingFlowParams);
                    }
                });
    }

    //Функция завершения работы с клиентом
    public void finish(){
        billingClient.endConnection();
    }
}
