package com.example.finances.course;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import maes.tech.intentanim.CustomIntent;


public class CourseRepeat extends AppCompatActivity {
    ImageButton close;
    Button next;
    ChipGroup groupRepeat;
    ChipGroup groupHow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_repeat);

        groupRepeat = findViewById(R.id.chipInputRep);
        groupHow = findViewById(R.id.chipInputHow);

        next = findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                Intent i = getIntent();
                int courseId = i.getIntExtra("COURSE_ID", -1);
                int lessons = i.getIntExtra("LESSONS", -1);

                Chip selectedChipRepeat = findViewById(groupRepeat.getCheckedChipId());
                Chip selectedChipHow = findViewById(groupHow.getCheckedChipId());

                Intent intent = new Intent(CourseRepeat.this, NewLessonActivity.class);
                intent.putExtra("COURSE_ID", courseId);
                intent.putExtra("LESSONS", lessons);
                intent.putExtra("CURRENT_LESSON", 0);
                intent.putExtra("COURSE_REPEAT", selectedChipRepeat.getText().toString().toUpperCase());
                intent.putExtra("COURSE_REPEAT_MODE", selectedChipHow.getText().toString().toUpperCase());
                startActivity(intent);
                CustomIntent.customType(CourseRepeat.this,"left-to-right");
                finish();

            }
        });


        close = findViewById(R.id.closeButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CourseRepeat.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(CourseRepeat.this,"fadein-to-fadeout");
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