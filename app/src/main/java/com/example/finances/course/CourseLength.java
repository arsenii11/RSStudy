package com.example.finances.course;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.ui.Account.course.LessonDateActivity;
import com.google.android.material.snackbar.Snackbar;

import maes.tech.intentanim.CustomIntent;


public class CourseLength extends AppCompatActivity {
    ImageButton close;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_length);

        final EditText contentEditText = findViewById(R.id.editTextCourseLength);

        next = findViewById(R.id.buttonNext2);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                String cn = contentEditText.getText().toString();
                int content = Integer.parseInt(cn);
                Log.e("LESSONS", cn);
                Log.e("LESSONS", String.valueOf(content));
                Intent i = getIntent();
                Course course = new Course();
                course.setName(i.getStringExtra("COURSE_NAME"));
                course.setLessons(content);

                DBHelper dbh = new DBHelper(getApplicationContext());

                if(dbh.insertCourse(course)) {
                    Snackbar snackbar = Snackbar.make(view1, "Record inserted successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    course = dbh.findCourse(course);
                    Intent intent = new Intent(CourseLength.this, LessonDateActivity.class);
                    intent.putExtra("COURSE_ID", course.getId());
                    intent.putExtra("LESSONS", course.getLessons());
                    intent.putExtra("CURRENT_LESSON", 0);
                    startActivity(intent);
                    CustomIntent.customType(CourseLength.this,"left-to-right");
                }
                else {
                    Snackbar snackbar = Snackbar.make(view1, "Record not inserted", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Intent intent = new Intent(CourseLength.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(CourseLength.this,"left-to-right");
                }

                finish();
            }
        });


        close = findViewById(R.id.closeButton2);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CourseLength.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(CourseLength.this,"fadein-to-fadeout");
                finish();

                if(CourseName.instance != null) {
                    try {
                        CourseName.instance.finish();
                    } catch (Exception e) {}
                }

            }
        });
    }
}