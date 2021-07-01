package com.example.finances.events.lesson;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.R;
import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.LessonOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

public class RescheduleLessonActivity extends AppCompatActivity {

    private int LESSON_ID; //ID урока

    Calendar startCalendar; //Календарь начала
    Calendar endCalendar; //Календарь конца

    TextView startDate; //Строка с датой начала
    TextView startTime; //Строка с временем начала
    TextView endTime; //Строка с датой конца

    Button next; //Кнопка дальше

    String COURSE_REPEAT; //Повторяется ли урок
    String COURSE_REPEAT_MODE; //Режим повторения

    ChipGroup groupRepeat; //Группа чипов для выбора потворения урока
    ChipGroup groupHow; //Группа чипов для выбора режима повторения урока

    DBHelper dbHelper; //Обработчик запросов к БД
    CalendarHelper calendarHelper; //Обработчик запрсоов к календарю

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_reschedule);

        startCalendar = Calendar.getInstance(); //Инициализируем календарь с датой начала
        endCalendar = Calendar.getInstance(); //Инициализируем календарь с датой конца

        dbHelper = new DBHelper(this); //Инициализируем обработчик к БД
        calendarHelper = new CalendarHelper(this); //Инициализируем обработчик к календарю

        startDate = findViewById(R.id.startDate); //Получаем из View Text View, предназначенный для даты начала
        startTime = findViewById(R.id.startTime); //Получаем из View Text View, предназначенный для времени начала
        endTime = findViewById(R.id.endTime); //Получаем из View Text View, предназначенный для времени конца

        next = findViewById(R.id.buttonLessonNext); //Получаем из View Button, предназначенную для подтверждения изменения

        groupRepeat = findViewById(R.id.chipInputRep); //Получаем из View ChipGroup, предназначенную для выбора переноса
        groupHow = findViewById(R.id.chipInputHow); //Получаем из View ChipGroup, предназначенную для выбора типа переноса
        Chip yesChip = findViewById(R.id.chip_yes); //Получаем из View чип с вариантом "yes"
        Chip noChip = findViewById(R.id.chip_no); //Получаем из View чип с вариантом "no"
        Chip monthChip = findViewById(R.id.chip_month); //Получаем из View чип с вариантом "monthly"
        Chip weekChip = findViewById(R.id.chip_week); //Получаем из View чип с вариантом "weekly"
        Chip weeks2Chip = findViewById(R.id.chip_2weeks); //Получаем из View чип с вариантом "every 2 weeks"

        LESSON_ID = getIntent().getIntExtra("LESSON_ID", -1); //Получаем ID урока из данных вызванного намерения

        Lesson lesson = dbHelper.getLesson(LESSON_ID); //Получаем экземпляр урока из БД
        LessonOptions lessonOptions = dbHelper.getLessonOptions(LESSON_ID); //Получаем экземпляр опций урока из БД

        startCalendar.setTimeInMillis(lesson.getDate()*1000); //Устанавливаем в календарь начала текущую дату урока
        endCalendar.setTimeInMillis(lesson.getDate()*1000 + (long) (lesson.getDuration()*3600000)); //Устанавливаем в календарь конца текущую дату урока + его длительность

        //В зависимости от повторения урока отмечаем нужный чип
        switch (lessonOptions.getIsRepeatable()){
            case 0: noChip.setChecked(true); break; //Урок не повторяется
            case 1: yesChip.setChecked(true); break; //Урок повторяется
        }

        //В зависимости от режима повторения урока отмечаем нужный чип
        switch (lessonOptions.getRepeatMode()){
            case 1: weekChip.setChecked(true); break; //Урок потворяется раз в неделю
            case 2: weeks2Chip.setChecked(true); break; //Урок повторяется раз в 2 недели
            case 3: monthChip.setChecked(true); break; //Урок повторяется раз в месяц
        }

        setInitialDateTime(); //Вызываем функцию установки даты начала и даты конца в TextView

        //Устанавливаем функцию при нажатии на кнопку дальше
        next.setOnClickListener(v -> {
            Chip selectedChipRepeat = findViewById(groupRepeat.getCheckedChipId()); //Ищем выделенный вариант потворения урока
            Chip selectedChipHow = findViewById(groupHow.getCheckedChipId()); //Ищем выделенный режим повторения урока

            COURSE_REPEAT = selectedChipRepeat.getText().toString().toUpperCase();
            COURSE_REPEAT_MODE = selectedChipHow.getText().toString().toUpperCase();

            Course course = dbHelper.getCourse(lesson.getCourseId()); //Получаем родительский курс из БД по его ID

            String currentTime = startTime.getText().toString(); //Получаем из TextView время начала урока
            if(currentTime.split(" ").length > 1){
                if(currentTime.split(" ")[1].equals("AM")) {
                    currentTime = currentTime.split(" ")[0];
                    if(currentTime.split(":")[0].equals("12")) currentTime = "0:" + currentTime.split(":")[1];
                }
                else{
                    currentTime = currentTime.split(" ")[0];
                    int hours = Integer.parseInt(currentTime.split(":")[0]) + 12;
                    currentTime = String.valueOf(hours) + ":" + currentTime.split(":")[1];
                }
            }

            String endTimeStr = endTime.getText().toString(); //Получаем из TextView время окончания урока
            if(endTimeStr.split(" ").length > 1){
                if(endTimeStr.split(" ")[1].equals("AM")) {
                    endTimeStr = endTimeStr.split(" ")[0];
                    if(endTimeStr.split(":")[0].equals("12")) endTimeStr = "0:"+endTimeStr.split(":")[1];
                }
                else{
                    endTimeStr = endTimeStr.split(" ")[0];
                    int hours = Integer.parseInt(endTimeStr.split(":")[0]) + 12;
                    endTimeStr = String.valueOf(hours) + ":" + endTimeStr.split(":")[1];
                }
            }

            //Рассчитываем длительность урока в формате HH:MM
            String dur = this.endTime.getText().toString();

            int hours = Integer.parseInt(endTimeStr.split(":")[0])-Integer.parseInt(currentTime.split(":")[0]);
            float minutes = Float.parseFloat(endTimeStr.split(":")[1]) - Float.parseFloat(currentTime.split(":")[1]);

            if(minutes<0){
                hours--;
                minutes += 60;
            }

            String hoursStr = String.valueOf(hours);
            String minutesStr = String.valueOf(minutes).split("\\.")[0];
            minutesStr = minutesStr.length() < 2 ? "0" + minutesStr : minutesStr;
            dur =  hoursStr + ":" + minutesStr;


            String lessonName = course.getName() + ", " + startDate.getText().toString() + ", " + startTime.getText().toString() + ", " + dur + " hours"; //Вычисляем имя урока
            lesson.setName(lessonName); //Устанавливаем имя урока


            long dat = startCalendar.getTimeInMillis()/1000; //Рассчитываем дату начала урока в секундах
            lesson.setDate(dat); //Устанавливаем дату начала

            //Рассчитываем и устанавливаем длительность урока в виде десятичной дроби
            try {
                lesson.setDuration(Integer.parseInt(dur.split(":")[0]) + Float.parseFloat(dur.split(":")[1]) / 60);
            } catch (Exception e){
                e.printStackTrace();
            }


            //Запускаем умное добавление урока в БД
            if(dbHelper.updateLessonSmart(LESSON_ID, lesson)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    calendarHelper.updateCalendarEvent(lessonOptions.getCalendarEventId(), course.getName(), startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
                }
                else{
                    calendarHelper.deleteCalendarEvent(lessonOptions.getCalendarEventId());
                    lessonOptions.setCalendarEventId(calendarHelper.addCalendarEvent(course.getName(), startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()));
                }
                lessonOptions.setIsRepeatable(0); //Ставим режим "не повторять"

                //Добавляем опции урока в БД
                if (COURSE_REPEAT.equals("YES")) {
                    lessonOptions.setIsRepeatable(1); //Устанавливаем режим "повторять"

                    //Рассчитываем добавочные миллисекудны и устанавливаем режим повтора урока в опции
                    long add = 0;
                    if (COURSE_REPEAT_MODE.equals("MONTHLY")) {
                        add = 1814400000L;
                        lessonOptions.setRepeatMode(3);
                    } else if (COURSE_REPEAT_MODE.equals("WEEKLY")) {
                        add = 604800000L;
                        lessonOptions.setRepeatMode(1);
                    } else if (COURSE_REPEAT_MODE.equals("EVERY 2 WEEKS")) {
                        add = 1209600000L;
                        lessonOptions.setRepeatMode(2);
                    }
                }
                dbHelper.updateLessonOptions(lessonOptions.getId(), lessonOptions); //Обновляем опции урока в БД
            }

            //Возврат на главный экран
            Intent intent = new Intent(RescheduleLessonActivity.this, LessonActivity.class);
            intent.putExtra("LESSON_ID", LESSON_ID);
            startActivity(intent);
            CustomIntent.customType(RescheduleLessonActivity.this,"left-to-right");
            finish();
        });
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                startCalendar.get(Calendar.HOUR_OF_DAY),
                startCalendar.get(Calendar.MINUTE), true)
                .show();
    }

    //отображаем диалоговое окно для выбора времени окончания
    public void setEndTime(View v) {
        new TimePickerDialog(this, te,
                endCalendar.get(Calendar.HOUR_OF_DAY),
                endCalendar.get(Calendar.MINUTE), true)
                .show();
    }


    // установка даты и времени в TextView
    private void setInitialDateTime() {

        //В TextView даты начала
        startDate.setText(DateUtils.formatDateTime(this,
                startCalendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));

        //В TextView времени начала
        startTime.setText(DateUtils.formatDateTime(this,
                startCalendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));

        //В TextView времени конца урока
        endTime.setText(DateUtils.formatDateTime(this,
                endCalendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = (view, hourOfDay, minute) -> {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCalendar.set(Calendar.MINUTE, minute);
            setInitialDateTime();
    };

    TimePickerDialog.OnTimeSetListener te = (view, hourOfDay, minute) -> {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endCalendar.set(Calendar.MINUTE, minute);
            setInitialDateTime();
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
    };
}
