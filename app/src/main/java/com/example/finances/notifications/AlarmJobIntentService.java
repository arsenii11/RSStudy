package com.example.finances.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

import org.jetbrains.annotations.NotNull;

public class AlarmJobIntentService extends JobIntentService {

    static final int JOB_ID = 9875;

    private static final String ACTION_ALARM =
            "android.intent.action.ACTION_ALARM";


    public static void startAction(Context context) {
        Intent intent = new Intent(context, AlarmJobIntentService.class);
        intent.setAction(ACTION_ALARM);
        enqueueWork(context, AlarmJobIntentService.class, JOB_ID,intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        Log.println(Log.ERROR, "onHandle()", "OK");
        final String action = intent.getAction();
        if (ACTION_ALARM.equals(action)) {
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

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
