package com.example.finances.ui.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.Test;
import com.example.finances.ui.Account.LessonAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    ArrayList<Lesson> lessons = new ArrayList<Lesson>();
    ArrayList<Test> tests = new ArrayList<Test>();
    com.example.finances.ui.Calendar.LessonAdapter lessonAdapter;
    TestAdapter testAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        setInitialData(calendar.getTimeInMillis());
        RecyclerView LessonsList = (RecyclerView) view.findViewById(R.id.Lessonlist);
        //RecyclerView TestsList = (RecyclerView) view.findViewById(R.id.Testlist);
        Context context = getContext();
        lessonAdapter = new com.example.finances.ui.Calendar.LessonAdapter(context, lessons);
        testAdapter = new TestAdapter(context, tests);
        LessonsList.setAdapter(lessonAdapter);
       // TestsList.setAdapter(testAdapter);

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setInitialData(calendar.getTimeInMillis());
                lessonAdapter = new com.example.finances.ui.Calendar.LessonAdapter(context, lessons);
                testAdapter = new TestAdapter(context, tests);
                LessonsList.setAdapter(lessonAdapter);
              //  TestsList.setAdapter(testAdapter);
            }
        });

        return view;
    }

    private void setInitialData(long datetime) {
        try {
            DBHelper dbHelper = new DBHelper(this.getContext());
            lessons = dbHelper.getLessonsFromDaySortByTime(datetime);
            tests = dbHelper.getTestsFromDaySortByTime(datetime);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}