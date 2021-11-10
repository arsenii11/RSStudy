package com.example.finances.events

import android.content.Context
import nz.co.trademe.covert.Covert
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.finances.R
import android.text.format.DateUtils
import android.content.Intent
import android.view.View
import com.example.finances.events.lesson.LessonActivity
import com.example.finances.events.test.TestActivity
import maes.tech.intentanim.CustomIntent
import android.widget.TextView
import com.example.finances.database.Event
import java.util.*

class MainAdaptor(
    private val context: Context,
    private val events: ArrayList<Event>,

    //Флаг показывать ли дату
    private val dateShow: Boolean,

    //Флаг включены ли свайпы
    private val swipeEnabled: Boolean,

    private val covert: Covert?,
    private val ACTIVITY: String
) : RecyclerView.Adapter<MainAdaptor.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.list_event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (swipeEnabled) covert?.drawCornerFlag(holder)

        val event = events[position] //Получаем текущий экземпляр события
        val eventType = event.eventType //Получаем тип события
        val calendar = Calendar.getInstance() //Создаем экземпляр календаря

        calendar.timeInMillis =
            event.date * 1000 //Устанавливаем в календарь дату и время начала события
        var name = event.name!!.split(", ").toTypedArray()[0] //Создаем строку с названием события

        //Создаем строку со временем начала события
        var fromTo = DateUtils.formatDateTime(
            context,
            calendar.timeInMillis,
            DateUtils.FORMAT_SHOW_TIME
        )

        //Создаем строку с датой начала события
        var date: String? = ""
        if (dateShow) date = DateUtils.formatDateTime(
            context,
            calendar.timeInMillis,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
        )

        //В зависимсоти от типа события меняем значения строк
        if (eventType === Event.EventType.Lesson) {
            name += " lesson" //Оставляем только название курса и приписываем слово lesson в конец
            calendar.timeInMillis =
                calendar.timeInMillis + (event.duration * 3600000).toLong() //Устанавливаем в календарь дату и время конца урока

            //Создаем строку со временем конца урока
            val to = DateUtils.formatDateTime(
                context,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_TIME
            )
            fromTo += " – $to" //Добавляем ко времени начала события время уонца
        }

        holder.nameView.text = name //Устанавливаем в View название события
        holder.eventId.text =
            java.lang.String.valueOf(event.eventId) //Устанавливаем в View id события
        holder.eventType.text = eventType!!.name.toUpperCase(Locale.ROOT) //Устанавливаем в View тип события
        holder.duration.text = fromTo //Устанавливаем в View время начала и конца (для урока)
        holder.date.text = date //Устанавливаем в View дату начала события

        //Создаем функцию при нажатии на элемент списка
        holder.itemView.setOnClickListener { v: View ->
            val intent: Intent //Создаем новое намерение

            //В зависимости от типа события создаем намерение перехода на новую активность
            if (eventType === Event.EventType.Lesson) {
                intent = Intent(
                    v.context,
                    LessonActivity::class.java
                ) //Переход на активность с информацией об уроке
                intent.putExtra("LESSON_ID", event.id) //Передаем в намерение id урока
            } else {
                intent = Intent(
                    v.context,
                    TestActivity::class.java
                ) //Переход на активность с информацией о тесте
                intent.putExtra("TEST_ID", event.id) //Предаем в намерение id теста
            }
            intent.putExtra("ACTIVITY", ACTIVITY)
            v.context.startActivity(intent) //Запускаем намерение
            CustomIntent.customType(v.context, "left-to-right") //Добавляем анимацию перехода
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }


    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        //Функция для оптимизации поиска объектов
        private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
            lazy(LazyThreadSafetyMode.NONE, initializer)

        val nameView //View с названием события
                : TextView by lazyUnsynchronized { view.findViewById<TextView>(R.id.LessonItem) }
        val eventId //View с id события (скрыто от пользователя)
                : TextView by lazyUnsynchronized { view.findViewById(R.id.EventID) }
        val eventType //View с типом события (скрыто от пользователя)
                : TextView by lazyUnsynchronized { view.findViewById(R.id.EventType) }
        val duration //View со временем начала и конца (для урока)
                : TextView by lazyUnsynchronized { view.findViewById(R.id.duration) }
        val date //View с датой начала события
                : TextView by lazyUnsynchronized { view.findViewById(R.id.date) }

    }


}