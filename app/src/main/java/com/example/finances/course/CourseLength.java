package com.example.finances.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.google.android.material.snackbar.Snackbar;

import maes.tech.intentanim.CustomIntent;


public class CourseLength extends AppCompatActivity {






    ImageButton close;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_length);




        final EditText contentEditText = findViewById(R.id.editTextLength);

        next = findViewById(R.id.buttonNext3);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                String content = contentEditText.getText().toString();
                Intent i = getIntent();
                Course course = new Course();
                course.setName(i.getStringExtra("COURSE_NAME"));
                course.setStartDate(i.getIntExtra("COURSE_DATE", -1));
                DBHelper dbh = new DBHelper(getApplicationContext());

                if(dbh.insertCourse(course)) {
                    Snackbar snackbar = Snackbar.make(view1, "Record inserted successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{
                Snackbar snackbar = Snackbar.make(view1, "Record not inserted", Snackbar.LENGTH_LONG);
                snackbar.show();}


                Intent intent = new Intent(CourseLength.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(CourseLength.this,"left-to-right");
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