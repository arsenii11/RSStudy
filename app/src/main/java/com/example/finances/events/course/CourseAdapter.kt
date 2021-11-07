package com.example.finances.events.course

import android.content.Context
import com.example.finances.database.Course
import nz.co.trademe.covert.Covert
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.finances.R
import android.content.Intent
import android.view.View
import com.example.finances.events.test.NewTestActivity
import com.example.finances.events.lesson.NewLessonActivity
import maes.tech.intentanim.CustomIntent
import android.widget.TextView
import java.util.ArrayList

class CourseAdapter(
    context: Context?,
    private val courses: ArrayList<Course>,
    private val mode: AdapterMode,
    private val swipeEnabled: Boolean,
    private val covert: Covert?,
    private val ACTIVITY: String
) : RecyclerView.Adapter<CourseAdapter.ViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    enum class AdapterMode {
        OpenCourse, AddLesson, AddTest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (swipeEnabled) covert?.drawCornerFlag(holder)
        val course = courses[position]
        holder.nameView.text = course.name
        holder.idView.text = course.id.toString()
        holder.itemView.setOnClickListener { v: View ->
            var intent = Intent(v.context, CourseActivity::class.java)
            when (mode) {
                AdapterMode.OpenCourse -> {
                    intent = Intent(v.context, CourseActivity::class.java)
                    v.onFinishTemporaryDetach()
                }
                AdapterMode.AddTest -> intent = Intent(v.context, NewTestActivity::class.java)
                AdapterMode.AddLesson -> intent = Intent(v.context, NewLessonActivity::class.java)
            }
            intent.putExtra("COURSE_ID", course.id)
            intent.putExtra("ACTIVITY", ACTIVITY)
            v.context.startActivity(intent)
            CustomIntent.customType(v.context, "left-to-right")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (!payloads.contains(Covert.SKIP_FULL_BIND_PAYLOAD)) {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView by lazy(LazyThreadSafetyMode.NONE) { view.findViewById(R.id.CourseName) }
        val idView: TextView by lazy(LazyThreadSafetyMode.NONE)  { view.findViewById(R.id.CourseID) }
    }

}