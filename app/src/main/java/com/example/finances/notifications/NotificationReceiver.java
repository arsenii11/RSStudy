package com.example.finances.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_SEND_ALARM = "ACTION_SEND_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {


        Notification.NotificationHelper notificationHelper = new Notification.NotificationHelper(context);
        //notificationHelper.createNotification();

    }
}