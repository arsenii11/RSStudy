package com.example.finances.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {


        AlarmJobIntentService.startAction(getApplicationContext());

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }
}
