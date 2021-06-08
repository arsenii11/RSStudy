package com.example.finances.ui.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.ui.Account.CourseAdapter;

import java.util.ArrayList;

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
        }
        CourseAdapter adapter = new CourseAdapter(this, courses, mode);
        CoursesList.setAdapter(adapter);
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
