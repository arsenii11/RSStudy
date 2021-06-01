package com.example.finances.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.helpclasses.NotificationChannel;
import com.example.finances.notifications.actions.NotificationCancelReceiver;

public class Notification {


    public static class NotificationHelper {

        private Context mContext;
        private static String CHANNEL_ID = "Info";
        private static final int NOTIFY_ID = 101;
        String title;
        String text;
        String bigText;

        NotificationHelper(Context context) {
            mContext = context;
        }




        void createNotification(String title, String text, String bixText) {

            //Create channel
            NotificationChannel.createNotification.createNotificationChannel(CHANNEL_ID,mContext);

            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Intent cancel = new Intent(mContext, NotificationCancelReceiver.class);
            cancel.putExtra("NOTIFY_ID", NOTIFY_ID);
            PendingIntent cancelP = PendingIntent.getBroadcast(mContext, 0, cancel, PendingIntent.FLAG_CANCEL_CURRENT);

            //title = "Привет!";
            //text = "Может быть ты уже что-то сделаешь для проекта,";
            //bigText = "Может быть ты уже что-то сделаешь для проекта, " + "а не только я все буду делать?";

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logobook_rmbg, options);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(mContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.outline_event_note_24)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setContentIntent(contentIntent)
                            .addAction(R.drawable.icons, "Открыть",cancelP)
                            .setLargeIcon(bitmap)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                            .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
            notificationManager.notify(NOTIFY_ID, builder.build());

        }
    }
}
