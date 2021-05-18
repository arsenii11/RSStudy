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

import maes.tech.intentanim.CustomIntent;


public class CourseDate extends AppCompatActivity {






    ImageButton close;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_date);


        final EditText contentEditText = findViewById(R.id.editTextStartDate);

        next = findViewById(R.id.buttonNext2);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String content = contentEditText.getText().toString();

                Intent intent = new Intent(CourseDate.this, CourseLength.class);
                intent.putExtra("COURSE_DATE", content);
                intent.putExtra("COURSE_NAME", getIntent().getStringExtra("COURSE_NAME"));


                startActivity(intent);
                CustomIntent.customType(CourseDate.this,"left-to-right");
            }
        });


        close = findViewById(R.id.closeButton1);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CourseDate.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(CourseDate.this,"fadein-to-fadeout");
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