package com.example.finances;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

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
