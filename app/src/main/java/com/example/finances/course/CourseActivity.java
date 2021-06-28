package com.example.finances.course;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Event;
import com.example.finances.database.LessonOptions;
import com.example.finances.toolbar.SettingsActivity;
import com.example.finances.ui.Calendar.MainAdaptor;

import java.util.ArrayList;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;


public class CourseActivity extends AppCompatActivity {

    private int COURSE_ID;

    ArrayList<NewEvent> newEvents = new ArrayList<NewEvent>();
    ArrayList<Event> events = new ArrayList<Event>();
    TextView labelCourse;

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
        setContentView(R.layout.course_info);

        labelCourse = findViewById(R.id.LableCourseName);

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
            mainAdaptor = new MainAdaptor(this, events, true, covert);
            eventsList.setAdapter(mainAdaptor);

            return null;
        }).attachTo(eventsList);


        mainAdaptor = new MainAdaptor(this, events, true, covert);
        eventsList.setAdapter(mainAdaptor);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setInitialData(){
        newEvents.add(new NewEvent ("lesson"));
        newEvents.add(new NewEvent ("test"));

        events = dbHelper.getAllEvents(COURSE_ID);

        labelCourse.setText(dbHelper.getCourse(COURSE_ID).getName());

    }

    public void intentNewLessonActivity(){
        Intent intent = new Intent(CourseActivity.this, NewLessonActivity.class);
        intent.putExtra("COURSE_ID", COURSE_ID);
        startActivity(intent);
        CustomIntent.customType(CourseActivity.this,"left-to-right");
        finish();
    }

    public void intentNewTestActivity() {
        Intent intent = new Intent(CourseActivity.this, NewTestActivity.class);
        intent.putExtra("COURSE_ID", COURSE_ID);
        startActivity(intent);
        CustomIntent.customType(CourseActivity.this,"left-to-right");
        finish();
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

    public void createCalendar(){
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");
        Cursor managedCursor = this.managedQuery(calendars, new String[] { "_id", "name" }, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst())
        {
            String calName;
            String calID;
            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");
            do
            {
                calName = managedCursor.getString(nameColumn);
                calID = managedCursor.getString(idColumn);
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        Calendar cal = Calendar.getInstance();
        long l = cal.getTimeInMillis();
        long cal_Id = 3;
        ContentValues event = new ContentValues();
        ContentResolver CR = getContentResolver();
        ContentValues calEvent  = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID,  cal_Id);
        calEvent.put(CalendarContract.Events.TITLE, "Demo Data");
        calEvent.put(CalendarContract.Events.DTSTART,l);
        calEvent.put(CalendarContract.Events.DTEND, l+60 * 1000);
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, "Indian/Christmas");
        Uri uri = CR.insert(Uri.parse("content://com.android.calendar/events"), calEvent);
        int id = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(this, "Created Calendar Event " + id,
                Toast.LENGTH_SHORT).show();
    }
}