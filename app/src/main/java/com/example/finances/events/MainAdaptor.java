package com.example.finances.events;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Event;
import com.example.finances.events.course.CourseActivity;
import com.example.finances.events.lesson.LessonActivity;
import com.example.finances.events.test.TestActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class MainAdaptor extends RecyclerView.Adapter<MainAdaptor.ViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<Event> events;
    private boolean swipeEnabled;
    private final Covert covert;


    public MainAdaptor(Context context, ArrayList<Event> events, boolean swipeEnabled, Covert covert)  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.events = events;
        this.covert = covert;
        this.swipeEnabled = swipeEnabled;
    }


    @NonNull
    @NotNull
    @Override
    public MainAdaptor.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_event_item, parent, false);
        return new MainAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        if(swipeEnabled)
            covert.drawCornerFlag(holder);

        Event event = events.get(position);
        Event.EventType eventType = event.getEventType();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getDate()*1000);

        String name = event.getName();
        String fromTo = DateUtils.formatDateTime(context,
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);

        if(eventType == Event.EventType.Lesson) {
            name = name.split(", ")[0] + " lesson";

            calendar.setTimeInMillis(calendar.getTimeInMillis() + (long) (event.getDuration()*3600000));

            String to = DateUtils.formatDateTime(context,
                    calendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_TIME);

            fromTo += " – " + to;
        }
        else {
            name = name.split(", ")[0];
        }



        holder.nameView.setText(name);
        holder.eventId.setText(String.valueOf(event.getEventId()));
        holder.eventType.setText(eventType.name().toUpperCase());
        holder.duration.setText(fromTo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if(eventType == Event.EventType.Lesson){
                intent = new Intent(v.getContext(), LessonActivity.class);
                intent.putExtra("LESSON_ID", event.getId());
            }
            else {
                intent = new Intent(v.getContext(), TestActivity.class);
                intent.putExtra("TEST_ID", event.getId());
            }
            v.getContext().startActivity(intent);
            CustomIntent.customType(v.getContext(),"left-to-right");
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView eventId;
        final TextView eventType;
        final TextView duration;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.LessonItem);
            eventId = view.findViewById(R.id.EventID);
            eventType = view.findViewById(R.id.EventType);
            duration = view.findViewById(R.id.duration);
        }
    }

}
