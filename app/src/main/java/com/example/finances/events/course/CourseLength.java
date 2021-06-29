package com.example.finances.events.course;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;

import maes.tech.intentanim.CustomIntent;


public class CourseLength extends AppCompatActivity {
    ImageButton close;
    Button next;
    Spinner spinner;
    String cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_length);

        //final EditText contentEditText = findViewById(R.id.editTextCourseLength);

        spinner = findViewById(R.id.spinner);
        String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cn = String.valueOf(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cn = "0";
            }
        });

        next = findViewById(R.id.buttonNext2);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //cn = contentEditText.getText().toString();
                int content = Integer.parseInt(cn);
                Log.e("LESSONS", cn);
                Log.e("LESSONS", String.valueOf(content));
                Intent i = getIntent();
                Course course = new Course();
                course.setName(i.getStringExtra("COURSE_NAME"));
                course.setLessons(content);

                DBHelper dbh = new DBHelper(getApplicationContext());

                if(dbh.insertCourse(course) && !cn.equals("0")) {
                    Snackbar snackbar = Snackbar.make(view1, "Record inserted successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    course = dbh.findCourse(course);
                    Intent intent = new Intent(CourseLength.this, NewLessonActivity.class);
                    intent.putExtra("COURSE_ID", course.getId());
                    intent.putExtra("LESSONS", course.getLessons());
                    intent.putExtra("CURRENT_LESSON", 0);
                    startActivity(intent);
                    CustomIntent.customType(CourseLength.this,"left-to-right");
                    finish();
                }
                else {
                    Snackbar snackbar = Snackbar.make(view1, "Record not inserted", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    Intent intent = new Intent(CourseLength.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(CourseLength.this,"left-to-right");
                    finish();
                }
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