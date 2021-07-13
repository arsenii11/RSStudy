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
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Test;
import com.example.finances.events.course.CourseActivity;
import com.example.finances.events.lesson.NewLessonActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class NewTestActivity extends AppCompatActivity {

    Calendar dateAndTime = Calendar.getInstance();
    TextView startDate;
    TextView startTime;
    Button next;
    int COURSE_ID;
    SwitchMaterial mode; //Переключатель вида теста

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_new);
        COURSE_ID = getIntent().getIntExtra("COURSE_ID", -1);

        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);

        mode = findViewById(R.id.modeSwitch);

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

                if (mode.isChecked()) {
                    test.setWeight(1);
                    testName += " exam";
                } else {
                    test.setWeight(0);
                    testName += " test";
                }


                testName += ", " + startDate.getText();
                test.setName(testName);
                dbHelper.insertTest(test);

                Intent intent = new Intent(NewTestActivity.this, CourseActivity.class);
                intent.putExtra("COURSE_ID", COURSE_ID);
                startActivity(intent);
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