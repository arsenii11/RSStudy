package com.example.finances.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.QuestionsActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.events.course.CourseNameActivity;
import com.example.finances.database.DBHelper;
import com.example.finances.events.course.CoursesListActivity;
import com.example.finances.ui.tutorial.TutorialFragment;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;


public class HomeFragment extends Fragment  {

    private final String ACTIVITY = "MAIN";

    PieChart pieChart;
    ImageButton plusCourse;
    ImageButton plusLesson;
    ImageButton plusTest;
    Calendar calendar;

    TextView nextEvent;
    TextView numberHours;
    TextView hr7days;
    ArrayList<Course> courses = new ArrayList<Course>();

    Button newCourse;
    Button ViewAllBt;

    ImageButton Questions;

    DBHelper dbHelper;
    CourseAdapter courseAdapter;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Context context = getContext();

        calendar = Calendar.getInstance();

        ViewAllBt = view.findViewById(R.id.ViewAllBt);

        Questions = view.findViewById(R.id.QuestionsBT);

        //Список
        setInitialData();
        RecyclerView CoursesList = (RecyclerView) view.findViewById(R.id.list);
        courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, false, null, ACTIVITY);

        // устанавливаем для списка адаптер
        CoursesList.setAdapter(courseAdapter);


        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        //TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        //titleshadow.setText("Home");


        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);


        plusCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), CourseNameActivity.class);

            intent.putExtra("ACTIVITY", ACTIVITY);

            startActivity(intent);
            CustomIntent.customType(getContext(),"fadein-to-fadeout");

        });

        plusLesson = view.findViewById(R.id.buttonpluslesson);
        plusLesson.setOnClickListener(v -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), CoursesListActivity.class);

            intent.putExtra("ADAPTER_MODE", "ADD_LESSON");
            intent.putExtra("ACTIVITY", ACTIVITY);

            startActivity(intent);
            CustomIntent.customType(getContext(),"fadein-to-fadeout");
        });

        plusTest = view.findViewById(R.id.buttonplustest);
        plusTest.setOnClickListener(v -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), CoursesListActivity.class);

            intent.putExtra("ADAPTER_MODE", "ADD_TEST");
            intent.putExtra("ACTIVITY", ACTIVITY);

            startActivity(intent);
            CustomIntent.customType(getContext(),"fadein-to-fadeout");
        });



        //Кнопка нового курса
        newCourse = view.findViewById(R.id.courseBt);
        newCourse.setClipToOutline(true);
        newCourse.setOnClickListener(v -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), CourseNameActivity.class);

            intent.putExtra("ACTIVITY", ACTIVITY);

            startActivity(intent);
            CustomIntent.customType(getContext(),"left-to-right");

        });

        Questions.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            Fragment tutorial = new TutorialFragment();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, tutorial).commit();
        });


        //Кнопка раскрывающая список курсов
        ViewAllBt.setOnClickListener(v -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), CoursesListActivity.class);

            intent.putExtra("ADAPTER_MODE", "OPEN_COURSE");

            startActivity(intent);
            CustomIntent.customType(getContext(),"fadein-to-fadeout");

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

        return view;
    }



    //добавляем значения
    private void setInitialData() {
        try {
            dbHelper = new DBHelper(this.getContext());
            courses = dbHelper.getAllActiveCourses();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}


