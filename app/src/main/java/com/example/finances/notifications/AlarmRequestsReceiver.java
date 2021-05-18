package com.example.finances.notifications;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AlarmRequestsReceiver extends BroadcastReceiver {

    public static final String ACTION_PERFORM_EXERCISE = "ACTION_PERFORM_EXERCISE";

    private static int sJobId = 1;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_PERFORM_EXERCISE:
                scheduleJob(context);
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
}
