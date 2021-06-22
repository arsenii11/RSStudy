package com.example.finances.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;

import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class NewEventAdapter extends RecyclerView.Adapter<NewEventAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<NewEvent> NewEventStates;
    private int COURSE_ID;

    NewEventAdapter(Context context,List<NewEvent> NewEventStates, int COURSE_ID){
        this.NewEventStates = NewEventStates;
        this.inflater = LayoutInflater.from(context);
        this.COURSE_ID = COURSE_ID;
    }
    public NewEventAdapter.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.plus_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewEventAdapter.ViewHolder holder, int position) {
        NewEvent newevent = NewEventStates.get(position);
        holder.newEventName.setText(newevent.getNewEventname());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              if(holder.newEventName.getText().toString().equals("lesson")){
                  Intent intent = new Intent(v.getContext(), NewLessonActivity.class);
                  intent.putExtra("COURSE_ID", COURSE_ID);
                  intent.putExtra("CURRENT_LESSON", 0);
                  intent.putExtra("LESSONS", 1);
                  intent.putExtra("COURSE_REPEAT", "NO");
                  v.getContext().startActivity(intent);
                  CustomIntent.customType(v.getContext(),"left-to-right");
              }
              else{
                  Intent intent = new Intent(v.getContext(), NewTestActivity.class);
                  intent.putExtra("COURSE_ID", COURSE_ID);
                  v.getContext().startActivity(intent);
                  CustomIntent.customType(v.getContext(),"left-to-right");
              }

            }
        });

    }

    @Override
    public int getItemCount() {
        return NewEventStates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView newEventName;
        ViewHolder(View view){
            super(view);
            newEventName = (TextView) view.findViewById(R.id.CourseName);

        }
    }
}


