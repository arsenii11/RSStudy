package com.example.finances.events.course;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Event;
import com.example.finances.database.LessonOptions;
import com.example.finances.events.newevent.NewEvent;
import com.example.finances.events.newevent.NewEventAdapter;
import com.example.finances.toolbar.SettingsActivity;
import com.example.finances.events.MainAdaptor;

import java.util.ArrayList;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;


public class CourseActivity extends AppCompatActivity {

    private int COURSE_ID;

    ArrayList<NewEvent> newEvents = new ArrayList<NewEvent>();
    ArrayList<Event> events = new ArrayList<Event>();

    TextView labelCourse;
    Button endCourse;

    Covert.Config config = new Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText);
    Covert covert;

    DBHelper dbHelper;
    CalendarHelper calendarHelper;

    MainAdaptor mainAdaptor;
    RecyclerView eventsList;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        labelCourse = findViewById(R.id.LabelCourseName);
        endCourse = findViewById(R.id.buttonEndCourse);

        eventsList = findViewById(R.id.allEventsList);
        eventsList.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        calendarHelper = new CalendarHelper(this);

        //Верхний тулбар
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
        TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        titleshadow.setVisibility(View.INVISIBLE);

        COURSE_ID = getIntent().getIntExtra("COURSE_ID", -1);

        setInitialData();

        RecyclerView recyclerView = findViewById(R.id.NewEventList);
        NewEventAdapter adapter = new NewEventAdapter(this, newEvents, COURSE_ID);
        recyclerView.setAdapter(adapter);

        covert = Covert.with(config).setIsActiveCallback(viewHolder -> false).doOnSwipe((viewHolder, swipeDirection) -> {
            TextView idView = viewHolder.itemView.findViewById(R.id.EventID);
            TextView typeView = viewHolder.itemView.findViewById(R.id.EventType);
            int eventId= Integer.parseInt(idView.getText().toString());
            String eventType = typeView.getText().toString();

            if (eventType.equals("LESSON")){
                LessonOptions lessonOptions = dbHelper.getLessonOptions(eventId);
                if (lessonOptions.getCalendarEventId() > -1)
                    calendarHelper.deleteCalendarEvent(lessonOptions.getCalendarEventId());
                dbHelper.deleteLesson(eventId);
            } else if (eventType.equals("TEST")){
                dbHelper.deleteTest(eventId);
            }

            setInitialData();
            mainAdaptor = new MainAdaptor(this, events, true, true, covert);
            eventsList.setAdapter(mainAdaptor);

            return null;
        }).attachTo(eventsList);

        endCourse.setOnClickListener(v -> {
            Course course = dbHelper.getCourse(COURSE_ID);
            course.setEndDate(Calendar.getInstance().getTimeInMillis()/1000);
            course.setFinished(1);
            dbHelper.updateCourse(COURSE_ID, course);
        });

        mainAdaptor = new MainAdaptor(this, events, true, true, covert);
        eventsList.setAdapter(mainAdaptor);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setInitialData(){
        newEvents.add(new NewEvent ("lesson"));
        newEvents.add(new NewEvent ("test"));

        events = dbHelper.getAllEvents(COURSE_ID);

        labelCourse.setText(dbHelper.getCourse(COURSE_ID).getName());

    }
}