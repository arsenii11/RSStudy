package com.example.finances.events.course;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class CourseListActivity extends AppCompatActivity {

    ArrayList<Course> courses = new ArrayList<Course>();
    DBHelper dbHelper;
    CalendarHelper calendarHelper;
    CourseAdapter courseAdapter;

    Covert.Config config = new Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText);
    Covert covert;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);
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



        //свайаы блин
        covert = Covert.with(config).setIsActiveCallback(viewHolder -> false).doOnSwipe((viewHolder, swipeDirection) -> {
            TextView textView = viewHolder.itemView.findViewById(R.id.CourseID);
            int id = Integer.parseInt(textView.getText().toString());
            calendarHelper.deleteAllCalendarEvent(id);
            dbHelper.deleteCourse(id);
            setInitialData();
            courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, true, covert);
            CoursesList.setAdapter(courseAdapter);
            return null;
        }).attachTo(CoursesList);

        courseAdapter = new CourseAdapter(this, courses, mode, true, covert);
        CoursesList.setAdapter(courseAdapter);

        //а это тулбар и фокусы с ним
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarCourseList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


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
            Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            dbHelper = new DBHelper(this);
            calendarHelper = new CalendarHelper(this);
            courses = dbHelper.getAllActiveCourses();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
