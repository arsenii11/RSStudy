package com.example.finances.ui.calendar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

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

    private final String ACTIVITY = "MAIN";

    CalendarView calendarView;
    ArrayList<Event> events = new ArrayList<Event>();
    MainAdaptor mainAdaptor;
    ImageView NoEvents;
    TextView NoEventsText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar calendar = Calendar.getInstance();

        NoEvents = view.findViewById(R.id.noevents);
        NoEventsText = view.findViewById(R.id.noeventsText);

        //Список курсов и уроков
        setInitialData(calendar.getTimeInMillis());
        RecyclerView LessonsList = (RecyclerView) view.findViewById(R.id.Lessonlist_calendar);
        Context context = getContext();
        mainAdaptor = new MainAdaptor(context, events, false, false, null, ACTIVITY);
        LessonsList.setAdapter(mainAdaptor);

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        titleshadow.setText("Calendar");



        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, month, dayOfMonth);
            setInitialData(calendar1.getTimeInMillis());
            mainAdaptor = new MainAdaptor(context, events, false, false, null, ACTIVITY);
            LessonsList.setAdapter(mainAdaptor);
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
                NoEventsText.setVisibility(View.VISIBLE);
            }
            else{
                NoEvents.setVisibility(View.INVISIBLE);
                NoEventsText.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}