package com.example.finances;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {


        Notification.NotificationHelper notificationHelper = new Notification.NotificationHelper(context);
        notificationHelper.createNotification();

    }
}