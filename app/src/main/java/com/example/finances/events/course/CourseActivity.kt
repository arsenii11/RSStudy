package com.example.finances.events.course
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
import maes.tech.intentanim.CustomIntent
import java.util.*

class CourseActivity : AppCompatActivity() {
    private var COURSE_ID = 0 //ID курса
    private val ACTIVITY = "COURSE" //Название активности

    private var labelCourse: TextView? = null //TextView для названия курса
    private var covert: Covert? = null //Для свайпов
    private var newEvents = ArrayList<NewEvent>() //Лист со всеми событиями
    private var calendarHelper: CalendarHelper? = null //Помощник по работе с календарем
    private var mainAdaptor: MainAdaptor? = null //Адаптер для списка событий
    private var config: Covert.Config = //Настройки для свайпов
        Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText)

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

    var events = ArrayList<Event>() //Список всех событий

    var dbHelper: DBHelper? = null //Помощник по работе с БД

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_info)

        //Поиск во View элемента для названия курса
        val labelCourse by lazyUnsynchronized { findViewById<TextView>(R.id.courseNameLabel) }
        //Поиск по View кнопки для завершения курса
        val endCourse by lazyUnsynchronized { findViewById<Button>(R.id.endCourseButton) }
        //Поиск во View элемента для отобраения списка событий
        val eventsList by lazyUnsynchronized { findViewById<RecyclerView>(R.id.allEventsList) }
        //DELETE THIS SHIT
        val newEventsList by lazyUnsynchronized { findViewById<RecyclerView>(R.id.newEventList) }

        eventsList.layoutManager = LinearLayoutManager(this)

        //Инициализация пощников
        dbHelper = DBHelper(this)
        calendarHelper = CalendarHelper(this)

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
        COURSE_ID = i.getIntExtra("COURSE_ID", -1)
        //ACTIVITY = i.getStringExtra("ACTIVITY");
        //Получаем информацию из БД
        setInitialData()


        //AND DELETE THIS SHIT
        val adapter = NewEventAdapter(this, newEvents, COURSE_ID, ACTIVITY)
        newEventsList.adapter = adapter

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
                    val lessonOptions = dbHelper!!.getLessonOptions(eventId)
                    //Проверяем, есть ли в календаре уведомление о выбранном уроке
                    if (lessonOptions.calendarEventId > -1)
                        //Удаляем событие в календаре
                        calendarHelper!!.deleteCalendarEvent(lessonOptions.calendarEventId)

                    //Удаляем урок
                    dbHelper!!.deleteLesson(eventId)
                }
                //В случае, если — тест
                else if (eventType == "TEST")
                    //Удаляем тест
                    dbHelper!!.deleteTest(eventId)

                //Получаем обновленную информацию из БД
                setInitialData()

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
            covert = covert!!,
            ACTIVITY = ACTIVITY
        )
        //Устанавливаем значения в список
        eventsList.adapter = mainAdaptor

        //Действия при клике на кнопку для завершения курса
        endCourse.setOnClickListener {
            //Получаем из БД информацию о текущем курсе
            val course = dbHelper!!.getCourse(COURSE_ID.toLong())
            //Устанавливаем текущую дату, как время завершения курса
            course.endDate = Calendar.getInstance().timeInMillis / 1000
            //Отмечаем, что курс завершен
            course.finished = 1
            //Обновляем информацию в БД
            dbHelper!!.updateCourse(COURSE_ID, course)
        }
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
            CustomIntent.customType(this, "fadein-to-fadeout")
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun setInitialData() {
        newEvents.add(NewEvent("lesson"))
        newEvents.add(NewEvent("test"))
        events = dbHelper!!.getAllEvents(COURSE_ID)
        labelCourse!!.text = dbHelper!!.getCourse(COURSE_ID.toLong()).name
    }
}