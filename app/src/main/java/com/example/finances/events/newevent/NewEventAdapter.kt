package com.example.finances.events.newevent

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.finances.R
import android.content.Intent
import android.view.View
import com.example.finances.events.lesson.NewLessonActivity
import maes.tech.intentanim.CustomIntent
import android.widget.TextView

class NewEventAdapter(
    context: Context?,
    private val NewEventStates: List<NewEvent>,
    private val COURSE_ID: Int,
    private val ACTIVITY: String
) : RecyclerView.Adapter<NewEventAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.plus_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newEvent = NewEventStates[position]
        holder.newEventName.text = newEvent.name
        holder.itemView.setOnClickListener { v: View ->
            val intent = Intent(v.context, NewLessonActivity::class.java)
            if (holder.newEventName.text.toString() == "lesson") {
                intent.putExtra("COURSE_ID", COURSE_ID)
                intent.putExtra("CURRENT_LESSON", 0)
                intent.putExtra("LESSONS", 1)
                intent.putExtra("COURSE_REPEAT", "NO")
            } else {
                intent.putExtra("COURSE_ID", COURSE_ID)
            }
            intent.putExtra("ACTIVITY", ACTIVITY)
            v.context.startActivity(intent)
            CustomIntent.customType(v.context, "left-to-right")
        }
    }

    override fun getItemCount(): Int {
        return NewEventStates.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val newEventName: TextView by lazy(LazyThreadSafetyMode.NONE) { view.findViewById(R.id.CourseName) }
    }

}