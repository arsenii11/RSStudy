package com.example.finances.ui.Statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticsFragment extends Fragment  {

    ImageButton plusCourse;
    ImageButton plusLesson;
    ImageButton plusTest;
    Calendar calendar;
    TextView dayofweek;
    TextView currenttime;
    TextView nextEvent;
    public static final String APP_PREFERENCES_Path = "Nickname" ;
    TextView numberHours;
    TextView hr7days;
    ArrayList<Course> courses = new ArrayList<Course>();

    Button newCourse;
    Button ViewAllBt;

    DBHelper dbHelper;
    CourseAdapter courseAdapter;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        Context context = getContext();

        calendar = Calendar.getInstance();

        ViewAllBt = view.findViewById(R.id.ViewAllBt);

        //переменные для виджета количества часов за 7 дней
        hr7days = view.findViewById(R.id.hoursthisweekText);
        numberHours = view.findViewById(R.id.hoursnumber);

        dayofweek = view.findViewById(R.id.dayofweek);
        currenttime = view.findViewById(R.id.textViewTime);

        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);

        //Список
        setInitialData();
        //RecyclerView CoursesList = view.findViewById(R.id.list);
        //courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, false, null);

        // устанавливаем для списка адаптер
        //CoursesList.setAdapter(courseAdapter);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        titleshadow.setText("Calendar");



        //widget time/day of week
        dayofweek.setText(setCurrentDate());
        currenttime.setText(setCurrentTime());
        task.run();

        return view;
    }


    private String setCurrentTime() {
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        return  timeText;
    }

    public String setCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek.substring(0,3);
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            dayofweek.setText(setCurrentDate());
            currenttime.setText(setCurrentTime());
            handler.postDelayed(this, 1000);
        }
    };
    //добавляем значения
    private void setInitialData() {
        dbHelper = new DBHelper(this.getContext());
        courses = dbHelper.getAllFinishedCourses();
        int duration = (int) dbHelper.getLessonDurationInTime(calendar.getTimeInMillis()-604800000, calendar.getTimeInMillis());
        numberHours.setText(String.valueOf(duration));
        if(duration==1)
            hr7days.setText("hour last\n 7 days");
        else
            hr7days.setText("hours last\n 7 days");
    }
}