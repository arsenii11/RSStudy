package com.example.finances;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.finances.database.DBHelper;
import com.example.finances.notifications.AlarmRequestsReceiver;
import com.example.finances.toolbar.About;
import com.example.finances.toolbar.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity  {



    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;
    private static final int MY_PERMISSIONS_WRITE_REQUEST = 1;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"WrongViewCast", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Обновляем состояние будильника
        myAlarm();



        int writePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((writePermissionCheck != PackageManager.PERMISSION_GRANTED))  {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_REQUEST); }

        //Проверка доступа к хранилищу
        int readPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_MEDIA); }
        else {
            readDataExternal();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void myAlarm() {

        /*Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 12);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30*1000, pendingIntent);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }*/

        Context appContext = getApplicationContext();

        Intent IntentForBroadcast =
                new Intent(appContext, AlarmRequestsReceiver.class);

        IntentForBroadcast
                .setAction(AlarmRequestsReceiver.ACTION_PERFORM_EXERCISE);

        appContext.sendBroadcast(IntentForBroadcast);
    }




    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume(){
        super.onResume();




        //Проверяем настройки
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flagTheme = prefs.getBoolean("aa",false);
        String theme = prefs.getString("ThemeList", "");
        if(flagTheme) {
            if (theme.equals("Bright")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (theme.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void readDataExternal() {
        //Можно потом подумать, но у меня все работает и без него
    }


    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA:
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                readDataExternal(); }
            break;
            default:
                break; } }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item4) {
            try {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(this,"fadein-to-fadeout");
                finish();

            } catch (Exception E) {

            }
        }

        if(id == R.id.item2){
            try{
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
                CustomIntent.customType(this,"fadein-to-fadeout");
            }

            catch (Exception E){

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


}