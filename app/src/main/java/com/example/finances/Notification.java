package com.example.finances;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finances.course.CourseLength;

public class Notification {


    public static class NotificationHelper {

        private Context mContext;
        private static String CHANNEL_ID = "Info";
        private static final int NOTIFY_ID = 101;

        NotificationHelper(Context context) {
            mContext = context;
        }




        void createNotification() {

            //Create channel
            NotificationChannel.createNotification.createNotificationChannel(CHANNEL_ID,mContext);

            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            String bigText = "Может быть ты уже что-то сделаешь для проекта, "
                    + "а не только я все буду делать?";

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logobook_rmbg, options);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.outline_event_note_24)
                            .setContentTitle("Привет!")
                            .setContentText("Может быть ты уже что-то сделаешь для проекта," )
                            .setContentIntent(contentIntent)
                            .addAction(R.drawable.icons, "Открыть",contentIntent)
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                            .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(NOTIFY_ID, builder.build());

        }
    }
}
