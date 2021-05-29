package com.example.finances.ui.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finances.R;
import com.example.finances.course.CourseName;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Test;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import maes.tech.intentanim.CustomIntent;
import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;

import static com.example.finances.design.diagram_colors.Diagram_colors;


public class HomeFragment extends Fragment  {



    PieChart pieChart;
    ImageButton plusCourse;
    Calendar calendar;
    TextView dayofweek;
    TextView currenttime;


    TextView nextEvent;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);
        plusCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), CourseName.class);
                startActivity(intent);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
            }
        });

        //Диаграма
        int DescriptionColor = getResources().getColor(R.color.diagramText);
        int myColor = getResources().getColor(R.color.hole);

        pieChart = (PieChart) view.findViewById(R.id.Piechart);
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

        pieChart.setData(data);

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

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        dayofweek.setText(dayOfTheWeek.substring(0,3));


        currenttime.setText(setCurrentime());


        return view;
    }

    private String setCurrentime() {
        Date currentDate = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        return  timeText;
    }
}
