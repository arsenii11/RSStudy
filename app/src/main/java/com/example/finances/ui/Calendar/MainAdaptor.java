package com.example.finances.ui.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Event;
import com.example.finances.database.Lesson;
import com.example.finances.database.Test;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        View view = inflater.inflate(R.layout.list_lesson_item, parent, false);
        return new MainAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        if(swipeEnabled)
            covert.drawCornerFlag(holder);

        Event event = events.get(position);
        String str = event.getName();
        if(str.split(", ").length > 3) {
            String name = str.split(", ")[0] + " lesson";
            String time = str.split(", ")[2];
            String dur = str.split(", ")[3];
            str = name + ", " + time + ", " + dur;
        }
        else {
            String name = str.split(", ")[0];
            String time = str.split(", ")[2];
            str = name + ", " + time;
        }
        holder.nameView.setText(str);
        holder.eventId.setText(String.valueOf(event.getEventId()));
        holder.eventType.setText(event.getEventType().name().toUpperCase());
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
