package com.example.finances.events.course

import com.example.finances.database.DBHelper
import com.example.finances.database.Course
import android.os.Bundle
import com.example.finances.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finances.ui.account.AccountActivity
import maes.tech.intentanim.CustomIntent
import java.util.ArrayList

class ClosedCoursesListActivity : AppCompatActivity() {
    var dbHelper: DBHelper? = null
    var courses = ArrayList<Course>()
    var courseAdapter: CourseAdapter? = null
    private val ACTIVITY = "CLOSED_COURSES"

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses_closed)

        //а это тулбар и фокусы с ним
        val toolbar by lazyUnsynchronized { findViewById<Toolbar>(R.id.toolbar) }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        setInitialData()
        val coursesList by lazyUnsynchronized { findViewById<RecyclerView>(R.id.list) }
        courseAdapter = CourseAdapter(
            this,
            courses,
            CourseAdapter.AdapterMode.OpenCourse,
            false,
            null,
            ACTIVITY
        )
        coursesList.adapter = courseAdapter
        val toolbarImage by lazyUnsynchronized { findViewById<ImageView>(R.id.toolbar_image) }
        toolbarImage.visibility = View.INVISIBLE
        val settings by lazyUnsynchronized { findViewById<ImageButton>(R.id.settings_bt) }
        settings.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.getItem(1).isVisible = false
        menu.getItem(0).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this, AccountActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //добавляем значения
    private fun setInitialData() {
        dbHelper = DBHelper(this)
        courses = dbHelper!!.allFinishedCourses
    }
}