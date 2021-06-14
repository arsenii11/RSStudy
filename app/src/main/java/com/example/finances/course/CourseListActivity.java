package com.example.finances.course;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class CourseListActivity extends AppCompatActivity {

    ArrayList<Course> courses = new ArrayList<Course>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses_list);
        setInitialData();
        RecyclerView CoursesList = (RecyclerView) findViewById(R.id.CoursesList);
        Context context = getApplicationContext();
        CourseAdapter.AdapterMode mode = CourseAdapter.AdapterMode.OpenCourse;
        Intent i = getIntent();
        String modeStr = i.getStringExtra("ADAPTER_MODE");
        switch (modeStr){
            case "ADD_LESSON": mode = CourseAdapter.AdapterMode.AddLesson; break;
            case "ADD_TEST": mode = CourseAdapter.AdapterMode.AddTest; break;
            case "OPEN_COURSE": mode = CourseAdapter.AdapterMode.OpenCourse; break;
            default: mode = CourseAdapter.AdapterMode.OpenCourse; break;
        }
        CourseAdapter adapter = new CourseAdapter(this, courses, mode);
        CoursesList.setAdapter(adapter);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCourseList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
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
        try {
            DBHelper dbHelper = new DBHelper(this.getApplicationContext());
            courses = dbHelper.getAllCourses();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
