package com.example.finances.events.course
import android.content.Intent
import com.example.finances.events.newevent.NewEvent
import android.widget.TextView
import com.example.finances.R
import nz.co.trademe.covert.Covert
import com.example.finances.database.DBHelper
import com.example.finances.calendar.CalendarHelper
import com.example.finances.events.MainAdaptor
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ImageButton
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finances.events.newevent.NewEventAdapter
import nz.co.trademe.covert.Covert.SwipeDirection
import com.example.finances.database.Event
import com.example.finances.events.lesson.NewLessonActivity
import com.example.finances.events.test.NewTestActivity
import maes.tech.intentanim.CustomIntent
import java.util.*
import kotlin.collections.ArrayList

class CourseActivity : AppCompatActivity() {

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)


    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_info)

        val ACTIVITY = "COURSE" //Название активности



        var mainAdaptor: MainAdaptor? //Адаптер для списка событий
        var covert: Covert? = null //Свайпы
        val config: Covert.Config = //Настройки для свайпов
            Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText)

        val events = ArrayList<Event>() //Список всех событий

        //Инициализация пощников
        val dbHelper = DBHelper(this)
        val calendarHelper = CalendarHelper(this)

        //Поиск во View элемента для названия курса
        val labelCourse by lazyUnsynchronized { findViewById<TextView>(R.id.courseNameLabel) }
        //Поиск по View кнопки для завершения курса
        val endCourse by lazyUnsynchronized { findViewById<Button>(R.id.endCourseButton) }
        //Поиск во View элемента для отобраения списка событий
        val eventsList by lazyUnsynchronized { findViewById<RecyclerView>(R.id.allEventsList) }
        //поиск кнопки нового урока
        val newlsBt by lazyUnsynchronized { findViewById<ImageButton>(R.id.newlessonBT) }
        //поиск кнопки нового теста
        val newtstBt by lazyUnsynchronized { findViewById<ImageButton>(R.id.newtestBT) }

        eventsList.layoutManager = LinearLayoutManager(this)

        //Верхний тулбар
        val toolbar by lazyUnsynchronized { findViewById<Toolbar>(R.id.toolbar) }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbarImage = findViewById<ImageView>(R.id.toolbar_image)
        toolbarImage.visibility = View.INVISIBLE
        val settings by lazyUnsynchronized { findViewById<ImageButton>(R.id.settings_bt) }
        settings.visibility = View.INVISIBLE
        val titleShadow by lazyUnsynchronized { toolbar.findViewById<TextView>(R.id.toolbar_shadowtext) }
        titleShadow.visibility = View.INVISIBLE



        //Получаем намерение, вызвавшее активность
        val i = intent
        //Получаем ID выбранного курса
        val COURSE_ID = i.getIntExtra("COURSE_ID", -1)
        //ACTIVITY = i.getStringExtra("ACTIVITY");

        //переходим к созданию нового УРОКА по кнопке
        newlsBt.setOnClickListener{
            val intent = Intent(this, NewLessonActivity::class.java)
            intent.putExtra("COURSE_ID", COURSE_ID)
            intent.putExtra("CURRENT_LESSON", 0)
            intent.putExtra("LESSONS", 1)
            intent.putExtra("COURSE_REPEAT", "NO")
            intent.putExtra("ACTIVITY", ACTIVITY)
            this.startActivity(intent)
            CustomIntent.customType(this, "left-to-right")
        }

        //переходим к созданию нового ТЕСТА по кнопке
        newtstBt.setOnClickListener{
            val intent = Intent(this, NewTestActivity::class.java)
            intent.putExtra("COURSE_ID", COURSE_ID)
            intent.putExtra("ACTIVITY", ACTIVITY)
            this.startActivity(intent)
            CustomIntent.customType(this, "left-to-right")
        }

        //Получаем информацию из БД
        setInitialData(
            events = events,
            labelCourse = labelCourse,
            COURSE_ID = COURSE_ID,
            dbHelper = dbHelper
        )






        //Конфигурация свайпов
        covert = Covert.with(config)
            .setIsActiveCallback { false }
            .doOnSwipe { viewHolder: RecyclerView.ViewHolder, swipeDirection: SwipeDirection? ->

                //Поиск во View элемента с ID события
                val idView by lazyUnsynchronized { viewHolder.itemView.findViewById<TextView>(R.id.EventID) }
                //Поиск во View элемента с типом события
                val typeView by lazyUnsynchronized { viewHolder.itemView.findViewById<TextView>(R.id.EventType) }

                //Получаем значение ID из TextView
                val eventId = idView.text.toString().toInt()
                //Получаем тип события из TextView
                val eventType = typeView.text.toString()

                //В зависимости от типа события настраиваем действия
                //В случае, если событие — урок
                if (eventType == "LESSON") {

                    //Получам из БД дополнительные парметры урока
                    val lessonOptions = dbHelper.getLessonOptions(eventId)
                    //Проверяем, есть ли в календаре уведомление о выбранном уроке
                    if (lessonOptions.calendarEventId > -1)
                        //Удаляем событие в календаре
                        calendarHelper.deleteCalendarEvent(lessonOptions.calendarEventId)

                    //Удаляем урок
                    dbHelper.deleteLesson(eventId)
                }
                //В случае, если — тест
                else if (eventType == "TEST")
                    //Удаляем тест
                    dbHelper.deleteTest(eventId)

                //Получаем обновленную информацию из БД
                setInitialData(
                    events = events,
                    labelCourse = labelCourse,
                    COURSE_ID = COURSE_ID,
                    dbHelper = dbHelper
                )

                //Обновляем адаптер списка событий
                mainAdaptor = MainAdaptor(this, events,
                    dateShow = true,
                    swipeEnabled = true,
                    covert = covert!!,
                    ACTIVITY = ACTIVITY
                )

                //Обновляем списки
                eventsList.adapter = mainAdaptor
            }.attachTo(eventsList)

        //Создаем адаптер списка событий на основе полученных данных из БД
        mainAdaptor = MainAdaptor(this, events,
            dateShow = true,
            swipeEnabled = true,
            covert = covert,
            ACTIVITY = ACTIVITY
        )
        //Устанавливаем значения в список
        eventsList.adapter = mainAdaptor

        //Действия при клике на кнопку для завершения курса
        endCourse.setOnClickListener {
            //Получаем из БД информацию о текущем курсе
            val course = dbHelper.getCourse(COURSE_ID.toLong())
            //Устанавливаем текущую дату, как время завершения курса
            course.endDate = Calendar.getInstance().timeInMillis / 1000
            //Отмечаем, что курс завершен
            course.finished = 1
            //Обновляем информацию в БД
            dbHelper.updateCourse(COURSE_ID, course)
        }
    }

    //Верхнее меню
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
            CustomIntent.customType(this, "fadein-to-fadeout")
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setInitialData(events: ArrayList<Event>, labelCourse: TextView, COURSE_ID: Int, dbHelper: DBHelper) {
        events.clear()
        events.addAll(dbHelper.getAllEvents(COURSE_ID))
        labelCourse.text = dbHelper.getCourse(COURSE_ID.toLong()).name
    }
}