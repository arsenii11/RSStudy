package com.example.finances.ui.Account;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.events.course.CourseListActivity;
import com.example.finances.events.course.CourseName;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.helpclasses.SquaredConstraintLayout;
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim;
import com.github.florent37.kotlin.pleaseanimate.core.Expectations;
import com.github.okdroid.checkablechipview.CheckableChipView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Signature;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment  {

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

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("home");

        //переменные для виджета количества часов за 7 дней
        hr7days = view.findViewById(R.id.hoursthisweekText);
        numberHours = view.findViewById(R.id.hoursnumber);

        dayofweek = view.findViewById(R.id.dayofweek);
        currenttime = view.findViewById(R.id.textViewTime);

        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);
        setInitialdata();



        //Диаграма
        int DescriptionColor = getResources().getColor(R.color.diagramText);
        int myColor = getResources().getColor(R.color.hole);







        //widget new event
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        /*TextView datetext = getActivity().findViewById(R.id.textdate);
        try {
            datetext.setText(mydate);}
        catch (Exception e){
            e.printStackTrace();
        }*/



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