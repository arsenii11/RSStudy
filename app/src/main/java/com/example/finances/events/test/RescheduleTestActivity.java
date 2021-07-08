package com.example.finances.events.test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Test;
import com.example.finances.events.course.CourseActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class RescheduleTestActivity extends AppCompatActivity {

    private int TEST_ID; //ID теста

    Calendar dateAndTime = Calendar.getInstance(); //Календарь с выбранной датой и временем
    TextView currentDateTime; //TextView с выбранной датой и временем

    DBHelper dbHelper; //Обработчик запросов к БД

    Button next; //Кнопка дальше

    ChipGroup chipInput; //Группа чипов для выбора типа теста: тест или экзамен

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reschedule);

        chipInput = findViewById(R.id.chipInput); //Получаем из View группу с чипами для выбора типа теста
        chipInput.setSingleSelection(true); //Устанавливаем одиночный режим для группы с чипами
        Chip testChip = findViewById(R.id.chip_test); //Получаем из View чип с вариантом "test"
        Chip examChip = findViewById(R.id.chip_exam); //Получаем из View чип с вариантом "exam"

        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);

        next = findViewById(R.id.buttonTestNext); //Получаем из View кнопку дальше
        currentDateTime = findViewById(R.id.currentDateTime); //получаем из View TextView, предназначенный для даты теста

        TEST_ID = getIntent().getIntExtra("TEST_ID", -1); //Получаем ID теста из переданных данных вызванного намерения

        dbHelper = new DBHelper(this); //Создаем новый обработчик запросов к БД

        Test test = dbHelper.getTest(TEST_ID); //Получаем экземпляр теста из БД по ID

        dateAndTime.setTimeInMillis(test.getDate()*1000); //Устанавливаем в календарь текущую дату теста

        //В зависимости от веса теста отмечаем нужный чип
        switch (test.getWeight()){
            case 0: testChip.setChecked(true); break; //Вес 0 — чип "test"
            case 1: examChip.setChecked(true); break; //Вес 1 — чип "exam"
        }

        setInitialDateTime(); //Вызываем функцию установки даты теста в TextView

        //Устанавливаем функцию при нажатии на кнопку дальше
        next.setOnClickListener(v -> {
            Chip selectedChip = findViewById(chipInput.getCheckedChipId()); //Получаем из View выбранный чип с видом теста

            Course course = dbHelper.getCourse(test.getCourseId()); //Получаем из БД родительский курс

            String testName = course.getName(); //Создаем новое название теста

            long dat = dateAndTime.getTimeInMillis()/1000; //Создаем новую дату теста
            test.setDate(dat); //Устанавливаем новую дату теста

            //В зависимости от выбранного вида теста устанавливаем название
            switch (selectedChip.getText().toString()){
                case "Test": test.setWeight(0); testName+= " test"; break; //Вес 0 — вид "test"
                case "Exam": test.setWeight(1); testName+= " exam"; break; //Вес 1 — вид "exam"
            }

            testName += ", " + currentDateTime.getText(); //Добавляем в название теста его дату и время
            test.setName(testName); //Устанавливаем новое название теста

            dbHelper.updateTest(TEST_ID, test); //Обновляем инофрмацию в БД новыми значениями

            Intent intent = new Intent(RescheduleTestActivity.this, TestActivity.class); //Создаем намерение перехода на активность с информацией о тесте
            intent.putExtra("TEST_ID", TEST_ID); //Передаем в намерение id теста
            startActivity(intent); //Запускаем намерение
            CustomIntent.customType(RescheduleTestActivity.this,"left-to-right"); //Добавляем анимацию к переходу
            finish();
        });

        //Что-то связанное с тулбаром
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
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
