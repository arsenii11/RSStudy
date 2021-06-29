package com.example.finances.ui.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finances.R;
import com.example.finances.events.course.CourseName;
import com.example.finances.database.DBHelper;
import com.example.finances.events.course.CourseListActivity;
import com.github.mikephil.charting.charts.PieChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        calendar = Calendar.getInstance();

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

       /* pieChart = (PieChart) view.findViewById(R.id.Piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        pieChart.setExtraOffsets(5,5,5,20);
        pieChart.setDragDecelerationFrictionCoef(0.95f);


        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(myColor);
        pieChart.setTransparentCircleRadius(50f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        //yValues.add(new PieEntry(34,""));
        yValues.add(new PieEntry(17,"C++"));
        yValues.add(new PieEntry(14,"English"));


        pieChart.animateY(1100, Easing.EaseInOutCirc);

        PieDataSet dataSet= new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors( Diagram_colors, getContext());


        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);*/

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

}


