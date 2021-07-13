package com.example.finances.events.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ClosedCourses extends AppCompatActivity {

    DBHelper dbHelper;
    ArrayList<Course> courses = new ArrayList<Course>();
    CourseAdapter courseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //а это тулбар и фокусы с ним
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCourseList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        setInitialData();
        RecyclerView CoursesList = findViewById(R.id.list);
        courseAdapter = new CourseAdapter(this, courses, CourseAdapter.AdapterMode.OpenCourse, false, null);
        CoursesList.setAdapter(courseAdapter);


        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
    }

  /*  @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RecyclerView CoursesList = (RecyclerView) findViewById(R.id.list);
        CoursesList.setVisibility(View.INVISIBLE);
        Snackbar snackbar = Snackbar.make(, "Hello Android", Snackbar.LENGTH_LONG);
        snackbar.show();
    }*/
    //добавляем значения
    private void setInitialData() {
        dbHelper = new DBHelper(this);
        courses = dbHelper.getAllFinishedCourses();
    }
}