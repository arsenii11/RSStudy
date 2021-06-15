package com.example.finances.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Course;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Course> courses;
    public enum AdapterMode{
        OpenCourse,
        AddLesson,
        AddTest
    }
    private final AdapterMode mode;
    private boolean swipeEnabled;
    private final Covert covert;

    public CourseAdapter(Context context, ArrayList<Course> courses, AdapterMode mode, boolean swipeEnabled, Covert covert) {
        this.courses = courses;
        this.inflater = LayoutInflater.from(context);
        this.mode = mode;
        this.covert = covert;
        this.swipeEnabled = swipeEnabled;
    }
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder holder, int position) {

        if(swipeEnabled)
            covert.drawCornerFlag(holder);

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
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull List<Object> payloads) {
        if (!payloads.contains(Covert.SKIP_FULL_BIND_PAYLOAD)) {
            onBindViewHolder(holder, position);
        }
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
