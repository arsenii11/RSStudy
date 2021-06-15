package com.example.finances.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Course;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Course> courses;
    public enum AdapterMode{
        OpenCourse,
        AddLesson,
        AddTest
    }
    private final AdapterMode mode;
    

    public CourseAdapter(Context context, ArrayList<Course> courses, AdapterMode mode) {
        this.courses = courses;
        this.inflater = LayoutInflater.from(context);
        this.mode = mode;
    }
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.nameView.setText(course.getName());

        holder.nameView.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CourseActivity.class);
                switch (mode){
                    case OpenCourse: intent = new Intent(v.getContext(), CourseActivity.class); break;
                    case AddTest: intent = new Intent(v.getContext(), NewTestActivity.class); break;
                    case AddLesson: intent = new Intent(v.getContext(), NewLessonActivity.class);
                }
                intent.putExtra("COURSE_ID", course.getId());
                v.getContext().startActivity(intent);
                CustomIntent.customType(v.getContext(),"left-to-right");
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.CourseName);
        }
    }
}
