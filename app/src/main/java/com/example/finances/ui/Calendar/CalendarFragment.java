package com.example.finances.ui.Calendar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Event;
import com.example.finances.events.MainAdaptor;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    ArrayList<Event> events = new ArrayList<Event>();
    MainAdaptor mainAdaptor;
    ImageView NoEvents;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar calendar = Calendar.getInstance();

        NoEvents = view.findViewById(R.id.noevents);

        //Список курсов и уроков
        setInitialData(calendar.getTimeInMillis());
        RecyclerView LessonsList = (RecyclerView) view.findViewById(R.id.Lessonlist_calendar);
        Context context = getContext();
        mainAdaptor = new MainAdaptor(context, events, false, false, null);
        LessonsList.setAdapter(mainAdaptor);

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                setInitialData(calendar.getTimeInMillis());
                mainAdaptor = new MainAdaptor(context, events, false, false, null);
                LessonsList.setAdapter(mainAdaptor);
            }
        });


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setInitialData(long datetime) {
        try {
            DBHelper dbHelper = new DBHelper(this.getContext());
            events = dbHelper.getEventFromDaySortByTime(datetime);
            if(events.size() == 0){
                NoEvents.setVisibility(View.VISIBLE);
            }
            else{
                NoEvents.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}