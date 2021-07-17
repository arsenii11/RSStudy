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

    Button next;
    ImageButton close;

    private String ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_name);

        final EditText contentEditText = findViewById(R.id.editTextCourseName);

        ACTIVITY = getIntent().getStringExtra("ACTIVITY");
        Intent finishIntent;
        switch (ACTIVITY){
            case "MAIN": finishIntent = new Intent(CourseName.this, MainActivity.class); break;
            default: finishIntent = new Intent(CourseName.this, MainActivity.class); break;
        }

        next = findViewById(R.id.buttonNext);
        next.setOnClickListener(v -> {
            String content = contentEditText.getText().toString();

            Intent intent = new Intent(CourseName.this, CourseLength.class);
            intent.putExtra("COURSE_NAME", content);
            intent.putExtra("ACTIVITY", ACTIVITY);
            startActivity(intent);
            CustomIntent.customType(CourseName.this,"left-to-right");
            finish();
        });

        close = findViewById(R.id.closeButton);
        close.setOnClickListener(v -> {
            startActivity(finishIntent);
            CustomIntent.customType(CourseName.this,"fadein-to-fadeout");
            finish();
        });

    }
}