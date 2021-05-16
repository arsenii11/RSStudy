package com.example.finances;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.concurrent.TimeUnit;

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

        ComponentName jobService = new ComponentName(context, JobSchedulerService.class);
        JobInfo.Builder exerciseJobBuilder = new JobInfo.Builder(sJobId++, jobService);


        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(exerciseJobBuilder.build());
    }
}
