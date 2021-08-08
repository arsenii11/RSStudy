package com.example.finances.purchases;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.R;

public class SubscribeActivity  extends AppCompatActivity {

    private boolean isSub;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        BillingClientHelper billingClientHelper = new BillingClientHelper(this);
        isSub = billingClientHelper.isSub(BillingClientHelper.SUBSCRIPTION_MONTH);

        if(!isSub){
            Button button = findViewById(R.id.subscribe);
            button.setOnClickListener(v -> {
                billingClientHelper.subscribe(SubscribeActivity.this, BillingClientHelper.SUBSCRIPTION_MONTH);
                billingClientHelper.finish();
            });
        }
    }
}
