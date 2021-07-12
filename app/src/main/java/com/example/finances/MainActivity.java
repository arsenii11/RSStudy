package com.example.finances;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.notifications.AlarmRequestsReceiver;
import com.example.finances.toolbar.SettingsActivity;
import com.example.finances.ui.Account;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

import static com.example.finances.ui.Statistics.StatisticsFragment.APP_PREFERENCES_Path;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {



    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;
    public static boolean ALLOW_ADD_TO_CALENDAR = false;


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

        //изображение на toolbar
        setToolbarImage();


        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this,"fadein-to-fadeout");
                finish();
            }
        });

        //регистрируем обработчик настроек
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        //Проверка разрешений
        if(checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) requestPermission();
        if(checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) requestPermission();
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermission();
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermission();

        ImageButton toolbar_button = toolbar.findViewById(R.id.toolbar_image_button);
        toolbar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AccOunt = new Intent(MainActivity.this, Account.class);
                startActivity(AccOunt);
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    public void myAlarm() {
        Context appContext = getApplicationContext();

        Intent IntentForBroadcast = new Intent(appContext, AlarmRequestsReceiver.class);

        IntentForBroadcast.setAction(AlarmRequestsReceiver.LESSON_ALARM);
        Log.e("Main", "check");
        appContext.sendBroadcast(IntentForBroadcast);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setToolbarImage();

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

        @Override
    protected void onDestroy(){
        super.onDestroy();
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




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



    //блок чтения настроек
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //устанавливаем никнейм
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nickname = prefs.getString("Nickname", "");
        TextView Nickname = findViewById(R.id.name);
        try {
        Nickname.setText(nickname);
        if (nickname.isEmpty()) {
            Nickname.setText("Nickname");
        }}
        catch (NullPointerException e){
            e.printStackTrace();
        }
        //устанавливаем email

        String email = prefs.getString("E-mail", "");
        TextView Email = findViewById(R.id.email);
        try {
            Email.setText(email);
            if (email.isEmpty()) {
                Email.setText("Not indicated");
            }
        }  catch (NullPointerException e){
            e.printStackTrace();
        }

        //устанавливаем место учебы
        String EduPLace = prefs.getString("Educational institution", "");
        TextView eduplace = findViewById(R.id.nickname);
        try {
            eduplace.setText(EduPLace);
            if (EduPLace.isEmpty()) {
                eduplace.setText("Not indicated");
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }


        //Проверяем настройки

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
        else {AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);}


        //активируем добавление событий в календарь
        ALLOW_ADD_TO_CALENDAR = prefs.getBoolean("AllowAddToCalendar", false);
    }

    //Запрашиваем разрешения
    private void requestPermission(){
        String[] permissions = new String[]{Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this,permissions,1);
    }

    public void setToolbarImage(){
        SharedPreferences accountPhoto = getSharedPreferences(APP_PREFERENCES_Path, Context.MODE_PRIVATE);
        String FilePath = accountPhoto.getString("key1", "");
        File Photo = new File(FilePath);
        if (Photo.exists()) {
            try {
                CircleImageView profileImage = findViewById(R.id.toolbar_image);
                Glide.with(this).load(Photo).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

