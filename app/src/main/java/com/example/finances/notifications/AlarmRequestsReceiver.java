package com.example.finances.notifications;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.LessonOptions;
import com.example.finances.database.Test;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmRequestsReceiver extends BroadcastReceiver {

    public static final String ACTION_PERFORM_EXERCISE = "ACTION_PERFORM_EXERCISE";
    public static final String LESSON_ALARM = "LESSON_ALARM";

    private static int sJobId = 1;


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
        ArrayList<Lesson> lessons = dbHelper.getAllLessons();
        Calendar now = Calendar.getInstance();

        for (Lesson lesson: lessons) {
            int hour = 3600000;
            long latency = lesson.getDate()*1000 - now.getTimeInMillis() - hour;
            Log.e("Latency", String.valueOf(latency) + " " + lesson.getDate()*1000 + " " + now.getTimeInMillis());
            if(latency>0) {
                String name = lesson.getName().split(", ")[0];
                PersistableBundle bundle = new PersistableBundle();
                bundle.putString("ACTION", AlarmJobIntentService.LESSON_ALARM);
                bundle.putString("TITLE", name + " lesson");
                bundle.putString("TEXT", "in an hour");
                bundle.putString("BIG_TEXT", "in an hour");
                bundle.putInt("LESSON_ID",lesson.getId());

                ComponentName jobService = new ComponentName(context, JobSchedulerService.class);
                JobInfo.Builder exerciseJobBuilder = new JobInfo.Builder(sJobId++, jobService);
                exerciseJobBuilder.setMinimumLatency(latency);
                exerciseJobBuilder.setExtras(bundle);

                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(exerciseJobBuilder.build());
                Log.e("Lesson", lesson.getName() + " " + lesson.getDate());
            }
        }
    }
}
