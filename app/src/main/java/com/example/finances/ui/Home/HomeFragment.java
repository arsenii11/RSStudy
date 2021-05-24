package com.example.finances.ui.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.finances.R;
import com.example.finances.course.CourseName;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

import static com.example.finances.design.diagram_colors.Diagram_colors;


public class HomeFragment extends Fragment  {



    PieChart pieChart;


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


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






        return view;
    }}
