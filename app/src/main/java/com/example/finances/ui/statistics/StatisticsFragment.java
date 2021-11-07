package com.example.finances.ui.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.R;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticsFragment extends Fragment  {

    private final String ACTIVITY = "MAIN";

    ImageButton plusCourse;
    ImageButton plusLesson;
    ImageButton plusTest;
    Calendar calendar;
    TextView dayofweek;
    private View view;
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
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        setProfileWidgetImage();

        calendar = Calendar.getInstance();

        ViewAllBt = view.findViewById(R.id.ViewAllBt);

        //переменные для виджета количества часов за 7 дней
        hr7days = view.findViewById(R.id.hoursthisweekText);
        numberHours = view.findViewById(R.id.hoursnumber);

        dayofweek = view.findViewById(R.id.dayofweek);
        currenttime = view.findViewById(R.id.textViewTime);

        //widget add something
        plusCourse = view.findViewById(R.id.buttonpluscourse);


        setInitialData();

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        titleshadow.setText("Statistics");

        //Виджет со статистикой по курсам

        ArrayList<TextView> courseViews = new ArrayList<>();
        courseViews.add(view.findViewById(R.id.course1));
        courseViews.add(view.findViewById(R.id.course2));
        courseViews.add(view.findViewById(R.id.course3));

        ArrayList<TextView> lsnViews = new ArrayList<>();
        lsnViews.add(view.findViewById(R.id.lsn1));
        lsnViews.add(view.findViewById(R.id.lsn2));
        lsnViews.add(view.findViewById(R.id.lsn3));

        int k = Math.min(courses.size(), 3);
        for(int i = 0; i < k; i++){
                Course course = courses.get(i);
                TextView courseView = courseViews.get(i);
                TextView lsnView = lsnViews.get(i);

                String courseText = course.getName() + "  " + (int) dbHelper.getLessonDurationByCourse(course.getId()) + " h";
                courseView.setText(courseText);

                String addTxt = course.getLessons() == 1 ? "lesson" : "lessons";
                String lessonText = course.getLessons() + " " + addTxt;
                lsnView.setText(lessonText);
        }

        //widget time/day of week
        dayofweek.setText(setCurrentDate());
        currenttime.setText(setCurrentTime());
        task.run();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        setProfileWidgetImage();

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
        courses = dbHelper.getCoursesSortByLessons();
        int duration = (int) dbHelper.getLessonDurationInTime(calendar.getTimeInMillis()-604800000, calendar.getTimeInMillis());
        numberHours.setText(String.valueOf(duration));
        if(duration==1)
            hr7days.setText("hour last\n 7 days");
        else
            hr7days.setText("hours last\n 7 days");
    }

    public void setProfileWidgetImage() {
        SharedPreferences accountPhoto = getActivity().getSharedPreferences(APP_PREFERENCES_Path, Context.MODE_PRIVATE);
        String FilePath = accountPhoto.getString("key1", "");
        File Photo = new File(FilePath);
        if (Photo.exists()) {
            try {
                CircleImageView profilewidgetImage = view.findViewById(R.id.profileWidgetImage);
                Glide.with(this).load(Photo).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(profilewidgetImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}