package com.example.finances.ui.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Lesson;
import com.example.finances.database.Test;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainAdaptor extends RecyclerView.Adapter<MainAdaptor.ViewHolder> {

    private final LayoutInflater inflater;
    private final ArrayList<String> array;


    public MainAdaptor(Context context, ArrayList<String> array)  {
        this.inflater = LayoutInflater.from(context);
        this.array = array;
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
        String str = array.get(position);
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
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.LessonItem);
        }
    }

}
