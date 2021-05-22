package com.example.finances.ui.Account.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.course.CourseLength;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;


public class LessonDateActivity extends AppCompatActivity {

    Calendar dateAndTime=Calendar.getInstance();
    TextView currentDateTime;
    EditText duration;
    Button next;
    int COURSE_ID;
    int LESSONS;
    int CURRENT_LESSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_length);
        Intent i = getIntent();
        COURSE_ID = i.getIntExtra("COURSE_ID", -1);
        LESSONS = i.getIntExtra("LESSONS", -1);
        CURRENT_LESSON = i.getIntExtra("CURRENT_LESSON", -1);

        currentDateTime=(TextView)findViewById(R.id.currentDateTime);
        duration = (EditText) findViewById(R.id.editTextLessonDuration);

        next = findViewById(R.id.buttonLessonNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                DBHelper dbHelper = new DBHelper(getApplicationContext());
                Intent i = getIntent();
                Lesson lesson = new Lesson();
                Course course = dbHelper.getCourse(COURSE_ID);
                String lessonName = course.getName() + " " + currentDateTime.getText();
                lesson.setName(lessonName);
                lesson.setCourseId(COURSE_ID);
                long dat = dateAndTime.getTimeInMillis()/1000;
                lesson.setDate(dat);
                lesson.setDuration(Integer.getInteger(duration.toString()));

                CURRENT_LESSON++;
                if(dbHelper.insertLesson(lesson) && CURRENT_LESSON<LESSONS) {
                    Snackbar snackbar = Snackbar.make(view1, "Record inserted successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Intent intent = new Intent(LessonDateActivity.this, LessonDateActivity.class);
                    intent.putExtra("COURSE_ID", COURSE_ID);
                    intent.putExtra("LESSONS", LESSONS);
                    intent.putExtra("CURRENT_LESSON", CURRENT_LESSON);
                    startActivity(intent);
                    CustomIntent.customType(LessonDateActivity.this,"left-to-right");
                }
                else {
                    Snackbar snackbar = Snackbar.make(view1, "Record not inserted", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Intent intent = new Intent(LessonDateActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(LessonDateActivity.this,"left-to-right");
                }

                finish();
            }
        });

        /*
        close = findViewById(R.id.closeButton3);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LessonDateActivity.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(LessonDateActivity.this,"fadein-to-fadeout");
                finish();
            }
        });*/
    }
}