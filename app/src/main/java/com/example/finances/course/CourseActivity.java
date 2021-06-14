package com.example.finances.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;

import maes.tech.intentanim.CustomIntent;


public class CourseActivity extends AppCompatActivity {

    private int COURSE_ID;
    /*
    public void addCalendarEvent() {

        long calID = 1;
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        ArrayList<Lesson> lessons = dbHelper.getAllLessons();
        for (Lesson lesson: lessons) {
            try {


            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, lesson.getDate());
            values.put(CalendarContract.Events.DTEND, lesson.getDate()+lesson.getDuration()*60*60*1000);
            values.put(CalendarContract.Events.TITLE, lesson.getName());
            values.put(CalendarContract.Events.DESCRIPTION, "RS:STUDIO");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Russia/Moscow");
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);}
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_info);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageButton newLessonBut = findViewById(R.id.ButtonNewLesson);
        ImageButton newTestBut = findViewById(R.id.ButtonNewTest);

        COURSE_ID = getIntent().getIntExtra("COURSE_ID", -1);
        newLessonBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, NewLessonActivity.class);
                intent.putExtra("COURSE_ID", COURSE_ID);
                startActivity(intent);
                CustomIntent.customType(CourseActivity.this,"left-to-right");
                finish();
            }
        });

        newTestBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, NewTestActivity.class);
                intent.putExtra("COURSE_ID", COURSE_ID);
                startActivity(intent);
                CustomIntent.customType(CourseActivity.this,"left-to-right");
                finish();
            }
        });

        //addCalendarEvent();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}