package com.example.finances.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    static String ACTION;
    static String TITLE;
    static String TEXT;
    static String BIG_TEXT;

    @Override
    public boolean onStartJob(JobParameters params) {

        PersistableBundle bundle = params.getExtras();
        ACTION = bundle.getString("ACTION");
        TITLE = bundle.getString("TITLE");
        TEXT = bundle.getString("TEXT");
        BIG_TEXT = bundle.getString("BIG_TEXT");

        Log.e("JobSchedulerService", "Check");
        Log.e("JobSchedulerService", ACTION);
        Log.e("JobSchedulerService", TITLE);
        Log.e("JobSchedulerService", TEXT);
        Log.e("JobSchedulerService", BIG_TEXT);

        AlarmJobIntentService.startAction(getApplicationContext(), ACTION, TITLE, TEXT, BIG_TEXT);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }
}
