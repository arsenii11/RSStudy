package com.example.finances.events.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.ui.Account.AccountActivity;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class ClosedCourses extends AppCompatActivity {

    DBHelper dbHelper;
    ArrayList<Course> courses = new ArrayList<Course>();
    CourseAdapter courseAdapter;

    private final String ACTIVITY = "CLOSED_COURSES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_closed);

        //а это тулбар и фокусы с ним
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCourseList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setInitialData();
        RecyclerView CoursesList = findViewById(R.id.list);
        courseAdapter = new CourseAdapter(this, courses, CourseAdapter.AdapterMode.OpenCourse, false, null, ACTIVITY);
        CoursesList.setAdapter(courseAdapter);


        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(this,"fadein-to-fadeout");
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //добавляем значения
    private void setInitialData() {
        dbHelper = new DBHelper(this);
        courses = dbHelper.getAllFinishedCourses();
    }
}