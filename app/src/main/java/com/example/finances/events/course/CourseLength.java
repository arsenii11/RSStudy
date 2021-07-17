package com.example.finances.events.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.events.lesson.NewLessonActivity;

import maes.tech.intentanim.CustomIntent;


public class CourseLength extends AppCompatActivity {
    ImageButton close; //Кнопка закрытия
    Button next; //кнопка дальше
    Spinner spinner; //меню выбора количества уроков в неделю
    int lessons; //Выбранное количество уроков в неделю

    private String ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_length);

        spinner = findViewById(R.id.spinner); //получаем из View меню для выбора количества уроков в неделю

        //Создаем новый адаптер для меню выбора количества уроков в неделю с предопределенными значениями
        String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Intent i = getIntent(); //Получаем намерение, которое вызвало эту активность
        ACTIVITY = i.getStringExtra("ACTIVITY"); //Получаем название активности, с которой началось действие
        Intent finishIntent;
        switch (ACTIVITY){
            case "MAIN": finishIntent = new Intent(CourseLength.this, MainActivity.class); break;
            default: finishIntent = new Intent(CourseLength.this, MainActivity.class); break;
        }

        spinner.setAdapter(adapter); //Устанавливаем адптер в меню
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lessons = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lessons = 0;
            }
        });

        next = findViewById(R.id.buttonNext2);
        next.setOnClickListener(v -> {

            Course course = new Course();
            course.setName(i.getStringExtra("COURSE_NAME"));

            DBHelper dbh = new DBHelper(getApplicationContext());
            long courseId = dbh.insertCourse(course);
            if( courseId != -1 && lessons != 0) {
                course = dbh.getCourse(courseId);
                Intent intent = new Intent(CourseLength.this, NewLessonActivity.class);
                intent.putExtra("COURSE_ID", course.getId());
                intent.putExtra("LESSONS", lessons);
                intent.putExtra("CURRENT_LESSON", 0);
                intent.putExtra("ACTIVITY", ACTIVITY);
                startActivity(intent);
            }
            else {
                startActivity(finishIntent);
            }
            CustomIntent.customType(CourseLength.this,"left-to-right");
            finish();
        });


        close = findViewById(R.id.closeButton2);
        close.setOnClickListener(v -> {
            startActivity(finishIntent);
            CustomIntent.customType(CourseLength.this,"fadein-to-fadeout");
            finish();

        });
    }
}