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

    static final String ACTION_ALARM =
            "android.intent.action.ACTION_ALARM";

    static final String LESSON_ALARM = "android.intent.action.LESSON_ALARM";


    public static void startAction(Context context, String action, String title, String text, String bigText) {
        Log.e("AlarmJobIntentService", "startActionCheck");
        Intent intent = new Intent(context, AlarmJobIntentService.class);
        //intent.setAction(ACTION_ALARM);
        intent.setAction(action);
        intent.putExtra("TITLE", title);
        intent.putExtra("TEXT", text);
        intent.putExtra("BIG_TEXT", bigText);
        enqueueWork(context, AlarmJobIntentService.class, JOB_ID, intent);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        Log.println(Log.ERROR, "onHandle()", "OK");
        final String action = intent.getAction();
        String title = intent.getStringExtra("TITLE");
        String text = intent.getStringExtra("TEXT");
        String bigText = intent.getStringExtra("BIG_TEXT");

        /*switch (action){
            case ACTION_ALARM: handleActionWriteAlarm();
            case LESSON_ALARM: lessonAlarm(title, text, bigText);
        }*/

        if(ACTION_ALARM.equals(action)) handleActionWriteAlarm();
        else if(LESSON_ALARM.equals(action)) lessonAlarm(title,text,bigText);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void handleActionWriteAlarm() {

        Notification.NotificationHelper notificationHelper = new Notification.NotificationHelper(getApplicationContext());
        notificationHelper.createNotification(null, null, null);

        //Context appContext = getApplicationContext();

        //Intent IntentForBroadcast = new Intent(appContext, NotificationReceiver.class);

        //IntentForBroadcast.setAction(NotificationReceiver.ACTION_SEND_ALARM);

        //appContext.sendBroadcast(IntentForBroadcast);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void lessonAlarm(String title, String text, String bigText) {

        Log.e("AlarmJobIntentService", "Check");
        Log.e("AlarmJobIntentService", title+" "+text+" "+bigText);
        Notification.NotificationHelper notificationHelper = new Notification.NotificationHelper(getApplicationContext());
        notificationHelper.createNotification(title, text, bigText);

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
