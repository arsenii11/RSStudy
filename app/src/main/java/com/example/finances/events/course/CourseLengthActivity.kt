package com.example.finances.events.course

import android.os.Bundle
import com.example.finances.R
import android.content.Intent
import android.view.View
import android.widget.*
import com.example.finances.MainActivity
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.example.finances.database.Course
import com.example.finances.database.DBHelper
import com.example.finances.events.lesson.NewLessonActivity
import maes.tech.intentanim.CustomIntent

class CourseLengthActivity : AppCompatActivity() {

    var lessons //Выбранное количество уроков в неделю
            = 0
    private var ACTIVITY: String? //Активность, с которой началось текущее действие (финишная активность)
            = null

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_length)

        //получаем из View меню для выбора количества уроков в неделю
        val spinner by lazyUnsynchronized {
            findViewById<Spinner>(R.id.lengthSpinner)
        }

        //Создаем новый адаптер для меню выбора количества уроков в неделю с предопределенными значениями
        val numbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val i = intent //Получаем намерение, которое вызвало эту активность

        ACTIVITY =
            i.getStringExtra("ACTIVITY") //Получаем название активности, с которой началось действие

        //Выбираем следующую активность в случае закрытия текущей
        val finishIntent: Intent = when (ACTIVITY) {
            "MAIN" -> Intent(this@CourseLengthActivity, MainActivity::class.java)
            else -> Intent(this@CourseLengthActivity, MainActivity::class.java)
        }

        spinner.adapter = adapter //Устанавливаем адптер в меню
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                lessons = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                lessons = 0
            }
        }

        //Получаем из View кнопку дальше
        val next by lazyUnsynchronized { findViewById<Button>(R.id.nextButton) }
        //Действие при клике на кнопку дальше
        next.setOnClickListener {

            //Создаем новый курс
            var course = Course()

            //Изменяем название нового курса на знечение, введенное на прошлой активности
            course.name = i.getStringExtra("COURSE_NAME")

            //Создаем помощника для общения с Базой Данных
            val dbh = DBHelper(applicationContext)

            //Добавляем в Базу Данных новый курс и получаем его ID
            val courseId = dbh.insertCourse(course)

            //В случае успеха и выбранным количеством уроков в неделю переходим на следующую активность
            if (courseId != -1L && lessons != 0) {

                //Получаем из базы данных созданный курс по его ID
                course = dbh.getCourse(courseId)

                //Создаем намерение перехода на активность создния урока
                val intent = Intent(this@CourseLengthActivity, NewLessonActivity::class.java)

                //Прикрепляем к намерению дополнительную инофрмацию
                intent.putExtra("COURSE_ID", course.id)
                intent.putExtra("LESSONS", lessons)
                intent.putExtra("CURRENT_LESSON", 0)
                intent.putExtra("ACTIVITY", ACTIVITY)

                //Запускаем намерение
                startActivity(intent)
            }
            //Иначе переходим на финишную активность
            else
                startActivity(finishIntent)

            CustomIntent.customType(this@CourseLengthActivity, "left-to-right")
            finish()
        }

        //Получаем из View кнопку закрыть
        val close by lazyUnsynchronized { findViewById<ImageButton>(R.id.closeButton) }
        //Действие при клике на кнопку закрыть
        close.setOnClickListener {

            //Запускаем намерение закрытия
            startActivity(finishIntent)
            CustomIntent.customType(this@CourseLengthActivity, "fadein-to-fadeout")
            finish()
        }
    }
}