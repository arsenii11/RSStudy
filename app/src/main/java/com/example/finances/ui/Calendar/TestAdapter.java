package com.example.finances.ui.Calendar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Lesson;
import com.example.finances.database.Test;
import com.example.finances.ui.Account.course.CourseActivity;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final ArrayList<Test> tests;

    TestAdapter(Context context, ArrayList<Test> tests) {
        this.tests = tests;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TestAdapter.ViewHolder holder, int position) {
        Test test = tests.get(position);
        holder.nameView.setText(test.getName());

        holder.nameView.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CourseActivity.class);
                intent.putExtra("TEST_ID", test.getId());
                v.getContext().startActivity(intent);
                CustomIntent.customType(v.getContext(),"left-to-right");
            }
        });
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.CourseName);
        }
    }
}
