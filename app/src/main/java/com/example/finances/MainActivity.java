package com.example.finances;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.adapty.Adapty;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.notifications.AlarmRequestsReceiver;
import com.example.finances.ui.calendar.CalendarFragment;
import com.example.finances.ui.subscribe.SubscribeActivity;
import com.example.finances.toolbar.SettingsActivity;
import com.example.finances.ui.account.AccountActivity;
import com.example.finances.ui.tutorial.TutorialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Console;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

import static com.example.finances.ui.statistics.StatisticsFragment.APP_PREFERENCES_Path;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static boolean ALLOW_ADD_TO_CALENDAR = false;


    @SuppressLint({"WrongViewCast", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adapty.activate(this,"public_live_TUnjqV0T.ayTCdvnOhehqQ2dQ4bI6");
        setContentView(R.layout.activity_main);


        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHost.getNavController();

        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.mobile_navigation);

        if (!getIntent().getBooleanExtra("VISITED", true))
            graph.setStartDestination(R.id.navigation_tutorial);
        else
            graph.setStartDestination(R.id.navigation_home);

        navController.setGraph(graph);
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navController);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Обновляем состояние будильника
        myAlarm();

        //изображение на toolbar
        setToolbarImage();

        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("ACTIVITY", "MAIN");
            startActivity(intent);
            CustomIntent.customType(MainActivity.this, "fadein-to-fadeout");
            finish();
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        //Проверка разрешений
        if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            requestPermission();
        if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            requestPermission();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermission();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermission();

        ImageButton toolbar_button = toolbar.findViewById(R.id.toolbar_image_button);
        toolbar_button.setOnClickListener(v -> {
            Intent Account = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(Account);
        });

    }


    public void myAlarm() {
        Intent IntentForBroadcast = new Intent(MainActivity.this, AlarmRequestsReceiver.class);

        IntentForBroadcast.setAction(AlarmRequestsReceiver.LESSON_ALARM);
        Log.e("Main", "check");
        this.sendBroadcast(IntentForBroadcast);
    }



    @Override
    protected void onStart() {
        super.onStart();

        myAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToolbarImage();

        //Проверяем настройки
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean flagTheme = prefs.getBoolean("aa", false);
        String theme = prefs.getString("ThemeList", "");
        if (flagTheme) {
            if (theme.equals("Bright")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (theme.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


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
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //устанавливаем email

        String email = prefs.getString("E-mail", "");
        TextView Email = findViewById(R.id.edutext);
        try {
            Email.setText(email);
            if (email.isEmpty()) {
                Email.setText("Not indicated");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //устанавливаем место учебы
        String EduPLace = prefs.getString("Educational institution", "");
        TextView eduPlace = findViewById(R.id.nickname);
        try {
            eduPlace.setText(EduPLace);
            if (EduPLace.isEmpty())
                eduPlace.setText("Not indicated");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //Проверяем настройки

        Boolean flagTheme = prefs.getBoolean("aa", false);
        String theme = prefs.getString("ThemeList", "");
        if (flagTheme) {
            if (theme.equals("Bright"))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            if (theme.equals("Dark"))
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);


        //активируем добавление событий в календарь
        ALLOW_ADD_TO_CALENDAR = prefs.getBoolean("AllowAddToCalendar", false);
    }

    //Запрашиваем разрешения
    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    public void setToolbarImage() {
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

