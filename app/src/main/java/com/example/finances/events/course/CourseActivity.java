package com.example.finances.events.course;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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