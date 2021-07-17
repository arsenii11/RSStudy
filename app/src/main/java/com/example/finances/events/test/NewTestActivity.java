package com.example.finances.events.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Test;
import com.example.finances.events.course.CourseActivity;
import com.example.finances.events.lesson.NewLessonActivity;
import com.example.finances.events.lesson.RescheduleLessonActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class NewTestActivity extends AppCompatActivity {

    private String ACTIVITY;

    Calendar dateAndTime = Calendar.getInstance();
    TextView startDate;
    TextView startTime;
    Button next;
    RadioGroup radioGroup; //Группа RadioButton для выбора test/exam
    int COURSE_ID;
    ImageButton exit; //выход из активности


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_new);

        Intent i = getIntent();
        COURSE_ID = i.getIntExtra("COURSE_ID", -1);
        ACTIVITY = i.getStringExtra("ACTIVITY");
        Intent finishIntent;
        switch (ACTIVITY){
            case "MAIN": finishIntent = new Intent(NewTestActivity.this, MainActivity.class); break;
            case "COURSE": finishIntent = new Intent(NewTestActivity.this, CourseActivity.class);
                finishIntent.putExtra("COURSE_ID", COURSE_ID);
                break;
            default: finishIntent = new Intent(NewTestActivity.this, MainActivity.class); break;
        }

        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);


        radioGroup = findViewById(R.id.radioGroupTstEx);

        next = findViewById(R.id.buttonTestNext);
        next.setOnClickListener(v -> {

            if(!startTime.getText().toString().contains("__") && !startDate.getText().toString().contains("__")) {

                DBHelper dbHelper = new DBHelper(this);
                Test test = new Test();

                Course course = dbHelper.getCourse(COURSE_ID);
                String testName = course.getName();

                test.setCourseId(COURSE_ID);
                long dat = dateAndTime.getTimeInMillis() / 1000;
                test.setDate(dat);

                RadioButton modeRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                String MODE =  modeRadioButton.getText().toString().toUpperCase();

                if (MODE.equals("EXAM")) {
                    test.setWeight(1);
                    testName += " exam";
                } else {
                    test.setWeight(0);
                    testName += " test";
                }


                testName += ", " + startDate.getText();
                test.setName(testName);
                dbHelper.insertTest(test);

                finishIntent.putExtra("COURSE_ID", COURSE_ID);
                startActivity(finishIntent);
                CustomIntent.customType(NewTestActivity.this, "left-to-right");
                finish();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTestActivity.this);
                builder.setTitle("Error!")
                        .setMessage("Please choose correct date and time")
                        .setCancelable(true)
                        .setNegativeButton("Ok", ((dialog, which) -> {
                            dialog.cancel();
                        }))
                        .create()
                        .show();
            }
        });

        exit = findViewById(R.id.buttonTestClose); //Ищем кнопку выхода из активности
        exit.setOnClickListener(v -> {
            CustomIntent.customType(NewTestActivity.this, "right-to-left");
            startActivity(finishIntent);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(this, "fadein-to-fadeout");
            return true;
        }
        return super.onOptionsItemSelected(item);
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