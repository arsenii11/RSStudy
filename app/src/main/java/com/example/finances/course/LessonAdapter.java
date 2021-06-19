package com.example.finances.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Lesson;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Lesson> lessons;

    public LessonAdapter(Context context, ArrayList<Lesson> lessons) {
        this.lessons = lessons;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public LessonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LessonAdapter.ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.nameView.setText(lesson.getName());

        holder.itemView.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CourseActivity.class);
                intent.putExtra("LESSON_ID", lesson.getId());
                v.getContext().startActivity(intent);
                CustomIntent.customType(v.getContext(),"left-to-right");
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.CourseName);
        }
    }
}
