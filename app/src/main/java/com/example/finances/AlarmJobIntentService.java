package com.example.finances;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finances.helpclasses.NotificationChannel;

import org.jetbrains.annotations.NotNull;

public class AlarmJobIntentService extends JobIntentService {

    static final int JOB_ID = 1000;

    private static final String ACTION_WRITE_ALARM =
            "com.github.ihandy.jobschedulerdemo.action.ACTION_WRITE_ALARM";


    public static void startAction(Context context) {
        Intent intent = new Intent(context, AlarmJobIntentService.class);
        intent.setAction(ACTION_WRITE_ALARM);

        enqueueWork(context, AlarmJobIntentService.class, JOB_ID,intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        final String action = intent.getAction();
        Toast.makeText(this, "work", Toast.LENGTH_SHORT).show();
        if (ACTION_WRITE_ALARM.equals(action)) {
            handleActionWriteAlarm();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void handleActionWriteAlarm() {

        Notification.NotificationHelper notificationHelper = new Notification.NotificationHelper(getApplicationContext());
        notificationHelper.createNotification();

        Context appContext = getApplicationContext();

        Intent IntentForBroadcast =
                new Intent(appContext, NotificationReceiver.class);

        IntentForBroadcast
                .setAction(NotificationReceiver.ACTION_SEND_ALARM);

        appContext.sendBroadcast(IntentForBroadcast);
    }
}
