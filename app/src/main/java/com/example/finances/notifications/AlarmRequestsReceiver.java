package com.example.finances.notifications;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;

import java.util.Calendar;

public class AlarmRequestsReceiver extends BroadcastReceiver {

    public static final String ACTION_PERFORM_EXERCISE = "ACTION_PERFORM_EXERCISE";
    public static final String LESSON_ALARM = "LESSON_ALARM";

    private static int sJobId = 1;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmRequestReceiver", "check1");
        switch (intent.getAction()) {
            case ACTION_PERFORM_EXERCISE:
                scheduleJob(context);
                break;
            case LESSON_ALARM:
                Log.e("AlarmRequestReceiver", "check2");
                lessonAlarm(context);
                break;
            default:
                throw new IllegalArgumentException("Unknown action.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void scheduleJob(Context context) {

        /*Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);*/

        ComponentName jobService = new ComponentName(context, JobSchedulerService.class);
        JobInfo.Builder exerciseJobBuilder = new JobInfo.Builder(sJobId++, jobService);
        exerciseJobBuilder.setMinimumLatency(60*1000);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(exerciseJobBuilder.build());
    }

    private void lessonAlarm(Context context){

        Log.e("AlarmRequestReceiver", "check3");

        DBHelper dbHelper = new DBHelper(context);
        Lesson lesson = dbHelper.getLessonFromNowSortByTime();
        Calendar now = Calendar.getInstance();
        long latency = lesson.getDate()*1000 - now.getTimeInMillis();

        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("ACTION", AlarmJobIntentService.LESSON_ALARM);
        bundle.putString("TITLE", lesson.getName()+" lesson");
        bundle.putString("TEXT", "Урок уже через час");
        bundle.putString("BIG_TEXT", "Урок уже через час, за 15 минут я напомню еще раз :)");

        ComponentName jobService = new ComponentName(context, JobSchedulerService.class);
        JobInfo.Builder exerciseJobBuilder = new JobInfo.Builder(sJobId++, jobService);
        exerciseJobBuilder.setMinimumLatency(30*1000);
        exerciseJobBuilder.setExtras(bundle);

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(exerciseJobBuilder.build());
    }
}
