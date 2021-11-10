package com.example.finances.ui.subscribe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.adapty.Adapty
import com.adapty.models.PaywallModel
import com.example.finances.R

class SubscribeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        val buttonMonth by lazy { findViewById<Button>(R.id.subscribeMonthButton) } //Кнопка для подписки на один месяц
        val buttonSemiannual by lazy { findViewById<Button>(R.id.subscribeSemiannualButton) } //Кнопка для подписки на полгода
        val buttonAnnual by lazy { findViewById<Button>(R.id.subscribeAnnualButton) } //Кнопка для подписки на год

        //Словарь для связи ID товара из Google Play и кнопки на экране
        val buttonsMap by lazy(LazyThreadSafetyMode.NONE) {
            mapOf(
                "rsstudy.month" to buttonMonth,
                "rsstudy.semiannual" to buttonSemiannual,
                "rsstudy.annual" to buttonAnnual
            )
        }

        //Получение досок с товарами
        Adapty.getPaywalls { paywalls, _, error ->
            if(error == null) {
                val paywall: PaywallModel? = paywalls?.firstOrNull { it.developerId == "paywall-main" } //Выбираем из всех досок основную

                //Проходим по каждому товару в основной доске
                paywall?.products?.forEach {

                    //Ищем в словаре кнопку с подходящим идентификатором товара и применяем к ней настройки
                    buttonsMap[it.vendorProductId]?.apply {
                        this.text = it.localizedPrice //Устанавливаем в поле текст цену товара в локальной валюте

                        //Добавляем действие при клике
                        this.setOnClickListener { _ ->

                            //Выполнение подписки
                            Adapty.makePurchase(this@SubscribeActivity, it) { purchaserInfo, purchaseToken, googleValidationResult, product, error ->
                                if (error == null) {

                                    //Проверка на успешность покупки
                                    if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
                                        TODO("Screen with congratulations") //Переход на экран с поздравлением
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
