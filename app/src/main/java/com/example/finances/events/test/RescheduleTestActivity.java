package com.example.finances.events.test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Test;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class RescheduleTestActivity extends AppCompatActivity {

    private int TEST_ID; //ID теста

    Calendar dateAndTime = Calendar.getInstance(); //Календарь с выбранной датой и временем
    TextView startDate; //TextView с выбранной датой
    TextView startTime; //TextView с выбранным временем

    DBHelper dbHelper; //Обработчик запросов к БД

    Button next; //Кнопка дальше

    SwitchMaterial mode; //Переключатель вида теста

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reschedule);


        next = findViewById(R.id.buttonTestNext); //Получаем из View кнопку дальше
        startDate = findViewById(R.id.startDate); //Получаем из View TextView, предназначенный для даты теста
        startTime = findViewById(R.id.startTime); //Получаем из View TextView, предназначенный для времени теста
        mode = findViewById(R.id.modeSwitch); //получаем из View SwitchMaterial, предназначенный для вида теста

        TEST_ID = getIntent().getIntExtra("TEST_ID", -1); //Получаем ID теста из переданных данных вызванного намерения

        dbHelper = new DBHelper(this); //Создаем новый обработчик запросов к БД

        Test test = dbHelper.getTest(TEST_ID); //Получаем экземпляр теста из БД по ID

        dateAndTime.setTimeInMillis(test.getDate()*1000); //Устанавливаем в календарь текущую дату теста

        //В зависимости от веса теста отмечаем нужный чип
        switch (test.getWeight()){
            case 0: mode.setChecked(false); break; //Вес 0 — чип "test"
            case 1: mode.setChecked(true); break; //Вес 1 — чип "exam"
        }

        setInitialDateTime(); //Вызываем функцию установки даты теста в TextView

        //Устанавливаем функцию при нажатии на кнопку дальше
        next.setOnClickListener(v -> {

            Course course = dbHelper.getCourse(test.getCourseId()); //Получаем из БД родительский курс

            String testName = course.getName(); //Создаем новое название теста

            long dat = dateAndTime.getTimeInMillis()/1000; //Создаем новую дату теста
            test.setDate(dat); //Устанавливаем новую дату теста

            //В зависимости от выбранного вида теста устанавливаем название
            if (mode.isChecked()) {
                test.setWeight(1);
                testName += " exam";
            } else {
                test.setWeight(0);
                testName += " test";
            }

            testName += ", " + startDate.getText(); //Добавляем в название теста его дату и время
            test.setName(testName); //Устанавливаем новое название теста

            dbHelper.updateTest(TEST_ID, test); //Обновляем инофрмацию в БД новыми значениями

            Intent intent = new Intent(RescheduleTestActivity.this, TestActivity.class); //Создаем намерение перехода на активность с информацией о тесте
            intent.putExtra("TEST_ID", TEST_ID); //Передаем в намерение id теста
            startActivity(intent); //Запускаем намерение
            CustomIntent.customType(RescheduleTestActivity.this,"left-to-right"); //Добавляем анимацию к переходу
            finish();
        });

        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        return true;
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {
        startDate.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        startTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
        dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateAndTime.set(Calendar.MINUTE, minute);
        setInitialDateTime();
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setInitialDateTime();
    };
}
