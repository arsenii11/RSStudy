package com.example.finances.events.course
import android.content.Intent
import com.example.finances.database.Course
import com.example.finances.database.DBHelper
import com.example.finances.calendar.CalendarHelper
import com.example.finances.R
import nz.co.trademe.covert.Covert
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.finances.events.course.CourseAdapter.AdapterMode
import nz.co.trademe.covert.Covert.SwipeDirection
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finances.MainActivity
import com.pchmn.materialchips.R2.id.home
import maes.tech.intentanim.CustomIntent
import java.util.ArrayList

class CoursesListActivity : AppCompatActivity() {

    private val ACTIVITY = "COURSE_LIST" //Название текущей активности
    var courses = ArrayList<Course>() //Список курсов
    var dbHelper: DBHelper? = null //Помощник по работе с БД

    private var calendarHelper: CalendarHelper? = null //Помощник по работе с календарем
    private var courseAdapter: CourseAdapter? = null //Адаптер для списка курсов
    private var config: Covert.Config = //Настройки для свайпов
        Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText)
    private var covert: Covert? = null //Свайпы

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses_list)

        //Получаем информацию из БД
        setInitialData()

        //Поиск во View элемента для отображения списка курсов
        val coursesList by lazyUnsynchronized { findViewById<RecyclerView>(R.id.coursesList) }
        //Поиск во View элемента с заголовком страницы
        val titleLabel by lazyUnsynchronized { findViewById<TextView>(R.id.titleLabel) }

        //Получаем намерение
        val i = intent
        //Создаем переменную для определения типа активности
        val mode: AdapterMode
        when (i.getStringExtra("ADAPTER_MODE")) {

            //Текущая активность была вызвана для добавления нового урока
            "ADD_LESSON" -> mode = AdapterMode.AddLesson
            //Текущая активность была вызвана для добавления нового теста
            "ADD_TEST" -> {
                mode = AdapterMode.AddTest
                titleLabel.text = "Choose a course to add a test to it"
            }
            //Текущая активность была вызвана, чтобы открыть курс
            "OPEN_COURSE" -> {
                mode = AdapterMode.OpenCourse
                titleLabel.text = "Choose a course"
            }
            //В любом другом случае тип будет определен как открытие курса
            else -> mode = AdapterMode.OpenCourse
        }

        //ACTIVITY = i.getStringExtra("ACTIVITY");

        //свайпы блин
        covert = Covert.with(config)
            .setIsActiveCallback { false }
            .doOnSwipe { viewHolder: RecyclerView.ViewHolder, swipeDirection: SwipeDirection? ->

                val textView by lazyUnsynchronized { viewHolder.itemView.findViewById<TextView>(R.id.CourseID) }
                val id = textView.text.toString().toInt()
                calendarHelper!!.deleteAllCalendarEvent(id)
                dbHelper!!.deleteCourse(id)
                setInitialData()
                courseAdapter =
                    CourseAdapter(this, courses, AdapterMode.OpenCourse, true, covert!!, ACTIVITY)
                coursesList.adapter = courseAdapter

            }.attachTo(coursesList)
        courseAdapter = CourseAdapter(this, courses, mode, true, covert!!, ACTIVITY)
        coursesList.adapter = courseAdapter

        //а это тулбар и фокусы с ним
        val toolbar by lazyUnsynchronized { findViewById<Toolbar>(R.id.toolbar) }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
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
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //добавляем значения
    private fun setInitialData() {
        dbHelper = DBHelper(this)
        calendarHelper = CalendarHelper(this)
        courses = dbHelper!!.allActiveCourses
    }
}