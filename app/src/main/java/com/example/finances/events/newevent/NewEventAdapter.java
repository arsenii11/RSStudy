package com.example.finances.events.newevent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.events.lesson.NewLessonActivity;
import com.example.finances.events.test.NewTestActivity;

import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class NewEventAdapter extends RecyclerView.Adapter<NewEventAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<NewEvent> NewEventStates;
    private final int COURSE_ID;
    private final String ACTIVITY;

    public NewEventAdapter(Context context, List<NewEvent> NewEventStates, int COURSE_ID, String ACTIVITY){
        this.NewEventStates = NewEventStates;
        this.inflater = LayoutInflater.from(context);
        this.COURSE_ID = COURSE_ID;
        this.ACTIVITY = ACTIVITY;
    }
    public NewEventAdapter.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.plus_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewEventAdapter.ViewHolder holder, int position) {
        NewEvent newevent = NewEventStates.get(position);
        holder.newEventName.setText(newevent.getNewEventname());
        holder.itemView.setOnClickListener(v -> {
          Intent intent = new Intent(v.getContext(), NewLessonActivity.class);
          if(holder.newEventName.getText().toString().equals("lesson")){
              intent.putExtra("COURSE_ID", COURSE_ID);
              intent.putExtra("CURRENT_LESSON", 0);
              intent.putExtra("LESSONS", 1);
              intent.putExtra("COURSE_REPEAT", "NO");
          }
          else{
              intent.putExtra("COURSE_ID", COURSE_ID);
          }

          intent.putExtra("ACTIVITY", ACTIVITY);
          v.getContext().startActivity(intent);
          CustomIntent.customType(v.getContext(),"left-to-right");
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


