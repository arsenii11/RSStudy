package com.example.finances.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;

import java.util.List;

public class NewEventAdapter extends RecyclerView.Adapter<NewEventAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<NewEvent> NewEventStates;

    NewEventAdapter(Context context,List<NewEvent> NewEventStates){
        this.NewEventStates = NewEventStates;
        this.inflater = LayoutInflater.from(context);
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


