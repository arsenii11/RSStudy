package com.example.finances.helpclasses;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;



public class NotificationChannel {


public static class createNotification{
   public static void createNotificationChannel(String Chn, Context s) {

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        int importance = NotificationManager.IMPORTANCE_HIGH;
        android.app.NotificationChannel channel = new android.app.NotificationChannel(Chn,"My notification", importance);
        channel.setDescription("Attempt 1");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = s.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }}
}
}
