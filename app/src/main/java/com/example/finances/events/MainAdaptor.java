package com.example.finances.events;

import android.content.Context;
import android.content.Intent;
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

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class MainAdaptor extends RecyclerView.Adapter<MainAdaptor.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<Event> events;
    private boolean swipeEnabled;
    private final Covert covert;


    public MainAdaptor(Context context, ArrayList<Event> events, boolean swipeEnabled, Covert covert)  {
        this.inflater = LayoutInflater.from(context);
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

        String str = event.getName();
        if(eventType == Event.EventType.Lesson) {
            String name = str.split(". ")[0] + " lesson";
            String time = str.split(". ")[2];
            String dur = str.split(". ")[3];
            str = name + ". " + time + ". " + dur;
        }
        else {
            String name = str.split(". ")[0];
            String time = str.split(". ")[2];
            str = name + ". " + time;
        }

        holder.nameView.setText(str);
        holder.eventId.setText(String.valueOf(event.getEventId()));
        holder.eventType.setText(eventType.name().toUpperCase());

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
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.LessonItem);
            eventId = view.findViewById(R.id.EventID);
            eventType = view.findViewById(R.id.EventType);
        }
    }

}
