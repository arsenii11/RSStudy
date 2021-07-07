package com.example.finances.ui.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.finances.database.Course;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.events.course.CourseName;
import com.example.finances.database.DBHelper;
import com.example.finances.events.course.CourseListActivity;
import com.example.finances.ui.Account.AccountFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import maes.tech.intentanim.CustomIntent;


public class HomeFragment extends Fragment  {



    PieChart pieChart;
    ImageButton plusCourse;
    ImageButton plusLesson;
    ImageButton plusTest;
    Calendar calendar;
    TextView dayofweek;
    TextView currenttime;
    TextView nextEvent;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Context context = getContext();

        calendar = Calendar.getInstance();

        ViewAllBt = view.findViewById(R.id.ViewAllBt);

        //Список
        setInitialData();
        RecyclerView CoursesList = (RecyclerView) view.findViewById(R.id.list);
        courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, false, null);

        // устанавливаем для списка адаптер
        CoursesList.setAdapter(courseAdapter);

        //переменные для виджета количества часов за 7 дней
        hr7days = view.findViewById(R.id.hoursthisweekText);
        numberHours = view.findViewById(R.id.hoursnumber);

        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);
        setInitialdata();

        plusCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), CourseName.class);
                startActivity(intent);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
                getActivity().finish();
            }
        });

        plusLesson = view.findViewById(R.id.buttonpluslesson);
        plusLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), CourseListActivity.class);
                intent.putExtra("ADAPTER_MODE", "ADD_LESSON");
                startActivity(intent);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
                getActivity().finish();
            }
        });

        plusTest = view.findViewById(R.id.buttonplustest);
        plusTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), CourseListActivity.class);
                intent.putExtra("ADAPTER_MODE", "ADD_TEST");
                startActivity(intent);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
                getActivity().finish();
            }
        });

        //Диаграма
        int DescriptionColor = getResources().getColor(R.color.diagramText);
        int myColor = getResources().getColor(R.color.hole);


        //Кнопка нового курса
        newCourse = view.findViewById(R.id.courseBt);
        newCourse.setClipToOutline(true);
        newCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), CourseName.class);
                startActivity(intent);
                CustomIntent.customType(getContext(),"left-to-right");
                getActivity().finish();
            }
        });


        //Кнопка раскрывающая список курсов
        ViewAllBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListCoursesList = new Intent(HomeFragment.this.getActivity(), CourseListActivity.class);
                ListCoursesList.putExtra("ADAPTER_MODE", "OPEN_COURSE");
                startActivity(ListCoursesList);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
                getActivity().finish();
            }
        });

        nextEvent = (TextView) view.findViewById(R.id.nextevent);
        try {
            DBHelper dbHelper = new DBHelper(getContext());
            String event = dbHelper.getEventFromNowSortByTimeStr();
            nextEvent.setText(event);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        //widget new event
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        TextView datetext = getActivity().findViewById(R.id.textdate);
        try {
        datetext.setText(mydate);}
        catch (Exception e){
            e.printStackTrace();
        }


        dayofweek = view.findViewById(R.id.dayofweek);
        currenttime = view.findViewById(R.id.textViewTime);
        //widget time/day of week


        //dayofweek.setText(setCurrentDate());
        //currenttime.setText(setCurrentTime());
        task.run();


        return view;
    }


    private void setInitialdata(){
        DBHelper dbHelper = new DBHelper(this.getContext());
        int duration = (int) dbHelper.getLessonDurationInTime(calendar.getTimeInMillis()-604800000, calendar.getTimeInMillis());
        numberHours.setText(String.valueOf(duration));
        if(duration==1){
        hr7days.setText("hour last\n 7 days");}
        else{hr7days.setText("hours last\n 7 days");}
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
        try {
            dbHelper = new DBHelper(this.getContext());
            courses = dbHelper.getAllCourses();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


