package com.example.finances.events.course

import android.os.Bundle
import com.example.finances.R
import android.widget.EditText
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.finances.MainActivity
import maes.tech.intentanim.CustomIntent

class CourseNameActivity : AppCompatActivity() {

    private var ACTIVITY: String? = null //Название активности, с которой началось действие

    //Функция для оптимизации поиска объектов
    private fun <T> lazyUnsynchronized(initializer: () -> T): Lazy<T> =
        lazy(LazyThreadSafetyMode.NONE, initializer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_name)

        //Получаем из View поле для ввода текста
        val courseName by lazyUnsynchronized { findViewById<EditText>(R.id.editTextCourseName) }

        //Получаем из намерения, название активности, с которой началось действие
        ACTIVITY = intent.getStringExtra("ACTIVITY")

        //Выбираем следующую активность в случае закрытия текущей
        val finishIntent: Intent = when (ACTIVITY) {
            "MAIN" -> Intent(this@CourseNameActivity, MainActivity::class.java)
            else -> Intent(this@CourseNameActivity, MainActivity::class.java)
        }

        //Получаем из View кнопку дальше
        val next by lazyUnsynchronized { findViewById<Button>(R.id.nextButton) }
        //Действие при клике на кнопку дальше
        next.setOnClickListener {

            //Записываем введенное значение названия курса в переменную
            val content = courseName.text.toString()

            //Создаем намерение перехода на активность с выбором уроков в неделю
            val intent = Intent(this@CourseNameActivity, CourseLengthActivity::class.java)

            //Прикрепляем к намерению дополнительную информацию
            intent.putExtra("COURSE_NAME", content)
            intent.putExtra("ACTIVITY", ACTIVITY)

            //Запускаем намерение
            startActivity(intent)
            CustomIntent.customType(this@CourseNameActivity, "left-to-right")
            finish()
        }

        //Получаем из View кнопку закрыть
        val close by lazyUnsynchronized { findViewById<Button>(R.id.closeButton) }
        //Действие при клике на кнопку закрыть
        close.setOnClickListener {

            //Запускаем намерение закрытия
            startActivity(finishIntent)
            CustomIntent.customType(this@CourseNameActivity, "fadein-to-fadeout")
            finish()
        }
    }
}