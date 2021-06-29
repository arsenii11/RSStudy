package com.example.finances.events.course;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.finances.MainActivity;
import com.example.finances.R;

import maes.tech.intentanim.CustomIntent;

public class CourseName extends AppCompatActivity {

    public static CourseName instance = null;

    Button next;
    ImageButton close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_name);

        instance = this;

        final EditText contentEditText = findViewById(R.id.editTextCourseName);

        next = findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentEditText.getText().toString();

                Intent intent = new Intent(CourseName.this, CourseLength.class);
                intent.putExtra("COURSE_NAME", content);
                startActivity(intent);
                CustomIntent.customType(CourseName.this,"left-to-right");
            }
        });

        close = findViewById(R.id.closeButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseName.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(CourseName.this,"fadein-to-fadeout");
                finish();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }
}