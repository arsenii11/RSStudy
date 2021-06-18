package com.example.finances.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.toolbar.SettingsActivity;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;


public class CourseActivity extends AppCompatActivity {

    private int COURSE_ID;

    ArrayList<NewEvent> newEvents = new ArrayList<NewEvent>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_info);

        //Верхний тулбар
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setInitialData();
        RecyclerView recyclerView = findViewById(R.id.NewEventList);
        NewEventAdapter adapter = new NewEventAdapter(this, newEvents);
        recyclerView.setAdapter(adapter);


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

    private void setInitialData(){
        newEvents.add(new NewEvent ("lesson"));
        newEvents.add(new NewEvent ("test"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item4) {
            try {
                Intent intent = new Intent(CourseActivity.this, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(this,"fadein-to-fadeout");


            } catch (Exception E) {

            }
        }
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