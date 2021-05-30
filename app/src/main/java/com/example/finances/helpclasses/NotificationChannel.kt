package com.example.finances.helpclasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannel {
    object createNotification {
        @JvmStatic
        fun createNotificationChannel(Chn: String?, s: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(Chn, "My notification", importance)
                channel.description = "Attempt 1"
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = s.getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}