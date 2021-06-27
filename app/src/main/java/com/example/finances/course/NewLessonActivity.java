package com.example.finances.course;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.preference.PreferenceManager;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.LessonOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;
import java.util.TimeZone;

import maes.tech.intentanim.CustomIntent;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

import static com.example.finances.MainActivity.ALLOW_ADD_TO_CALENDAR;


public class NewLessonActivity extends AppCompatActivity {

    Calendar dateAndTime; //Календарь начала
    Calendar timeEnd; //Календарь конца
    TextView currentDateTime; //Строка с датой начала
    TextView endDateTime; //Строка с датой конца
    Button next; //Кнопка дальше
    int COURSE_ID; //ID родительского курса
    int LESSONS; //Количество уроков, которые нужно сейчас добавить
    int CURRENT_LESSON; //Текущее количество добавленных уроков
    String COURSE_REPEAT; //Повторяется ли урок
    String COURSE_REPEAT_MODE; //Режим повторения
    ChipGroup groupRepeat; //Группа чипов для выбора потворения урока
    ChipGroup groupHow; //Группа чипов для выбора режима повторения урока

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lesson);

        //Устанавливаем текущую дату и время в календари
        dateAndTime = Calendar.getInstance();
        timeEnd = Calendar.getInstance();

        //Получаем значения из intent
        Intent i = getIntent();
        COURSE_ID = i.getIntExtra("COURSE_ID", -1);
        LESSONS = i.getIntExtra("LESSONS", -1);
        CURRENT_LESSON = i.getIntExtra("CURRENT_LESSON", -1);

        currentDateTime=(TextView)findViewById(R.id.currentDateTime); //Ищем TextView для даты начала
        endDateTime = (TextView) findViewById(R.id.endDateTime); //Ищем TextView для даты конца

        groupRepeat = findViewById(R.id.chipInputRep); //Ищем группу чипов для выбора повторения урока
        groupHow = findViewById(R.id.chipInputHow); //Ищем группу чипов для выбора режма повторения урока

        next = findViewById(R.id.buttonLessonNext); //Ищем кнопку дальше

        //Назначаем действия при клике на кнопку дальше
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                Chip selectedChipRepeat = findViewById(groupRepeat.getCheckedChipId()); //Ищем выделенный вариант потворения урока
                Chip selectedChipHow = findViewById(groupHow.getCheckedChipId()); //Ищем выделенный режим повторения урока

                COURSE_REPEAT = selectedChipRepeat.getText().toString().toUpperCase();
                COURSE_REPEAT_MODE = selectedChipHow.getText().toString().toUpperCase();

                DBHelper dbHelper = new DBHelper(getApplicationContext()); //заполняем БД
                Lesson lesson = new Lesson(); //Создаем пустой урок
                Course course = dbHelper.getCourse(COURSE_ID); //Получаем родительский курс из БД по его ID

                String currentTime = currentDateTime.getText().toString().split(", ")[1]; //Получаем из TextView время начала урока
                if(currentDateTime.getText().toString().split(", ").length == 3) currentTime = currentDateTime.getText().toString().split(", ")[2]; //проверка на формат даты вида "June 16, 2021"
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

                String endTime = endDateTime.getText().toString(); //Получаем из TextView время окончания урока
                if(endTime.split(" ").length > 1){
                    if(endTime.split(" ")[1].equals("AM")) {
                        endTime = endTime.split(" ")[0];
                        if(endTime.split(":")[0].equals("12")) endTime = "0:"+endTime.split(":")[1];
                    }
                    else{
                        endTime = endTime.split(" ")[0];
                        int hours = Integer.parseInt(endTime.split(":")[0]) + 12;
                        endTime = String.valueOf(hours) + ":" + endTime.split(":")[1];
                    }
                }
                //Рассчитываем длительность урока в формате HH:MM
                String dur = endDateTime.getText().toString();

                int hours = Integer.parseInt(endTime.split(":")[0])-Integer.parseInt(currentTime.split(":")[0]);
                float minutes = Float.parseFloat(endTime.split(":")[1]) - Float.parseFloat(currentTime.split(":")[1]);
                String hoursStr = String.valueOf(hours);
                String minutesStr = String.valueOf(minutes).split("\\.")[0];
                minutesStr = minutesStr.length() < 2 ? "0" + minutesStr : minutesStr;
                dur =  hoursStr + ":" + minutesStr;


                String lessonName = course.getName() + ", " + currentDateTime.getText().toString() + ", " + dur + " hours"; //Вычисляем имя урока
                lesson.setName(lessonName); //Устанавливаем имя урока

                lesson.setCourseId(COURSE_ID); //устанавливаем уроку ID родительского курса

                long dat = dateAndTime.getTimeInMillis()/1000; //Рассчитываем дату начала урока в секундах
                lesson.setDate(dat); //Устанавливаем дату начала

                //Рассчитываем и устанавливаем длительность урока в виде десятичной дроби
                try {
                    lesson.setDuration(Integer.parseInt(dur.split(":")[0]) + Float.parseFloat(dur.split(":")[1]) / 60);
                } catch (Exception e){
                    e.printStackTrace();
                }


                //Запускаем умное добавление урока в БД
                if(dbHelper.insertLessonSmart(lesson)){

                    CURRENT_LESSON++; //Увеличиваем счетчик установленных уроков

                    LessonOptions lessonOptions = new LessonOptions(); //Создаем новые опции для урока

                    lesson = dbHelper.findLesson(lesson); //Ищем добавленный урок

                    lessonOptions.setLessonId(lesson.getId()); //Устанавливаем id урока в опции
                    lessonOptions.setCalendarEventId(addCalendarEvent(course.getName() + " lesson", dateAndTime.getTimeInMillis(), timeEnd.getTimeInMillis())); //Устанавливаем ID события в календаре
                    lessonOptions.setIsRepeatable(0); //Ставим режим "не повторять"

                    if(COURSE_REPEAT.equals("YES")) {

                        lessonOptions.setIsRepeatable(1); //Устанавливаем режим "повторять"

                        //Рассчитываем добавочные миллисекудны и устанавливаем режим повтора урока в опции
                        long add = 0;
                        if(COURSE_REPEAT_MODE.equals("MONTHLY")) { add = 1814400000L; lessonOptions.setRepeatMode(3); }
                        else if(COURSE_REPEAT_MODE.equals("WEEKLY")) { add = 604800000L; lessonOptions.setRepeatMode(1); }
                        else if(COURSE_REPEAT_MODE.equals("EVERY 2 WEEKS")) { add = 1209600000L; lessonOptions.setRepeatMode(2);}

                        dbHelper.insertLessonOptions(lessonOptions); //Добавляем опции урока в БД

                        //Добавляем к календарям время переноса
                        dateAndTime.setTimeInMillis(dateAndTime.getTimeInMillis() + add);
                        timeEnd.setTimeInMillis(timeEnd.getTimeInMillis() + add);
                        setInitialDateTime();

                        lessonName = course.getName() + ", " + currentDateTime.getText().toString() + ", " + endDateTime.getText().toString() + " hours"; //Вычисляем имя перенесенного урока
                        lesson.setName(lessonName); //Устанавливаем имя перенесенного урока

                        dat = dateAndTime.getTimeInMillis()/1000; //Рассчитываем дату начала перенесенного уока в секундах
                        lesson.setDate(dat); //Устанавливаем дату начала перенесенного урока

                        //dbHelper.insertLessonSmart(lesson); //Запускаем умное добавление перенесенного урока

                        //lesson = dbHelper.findLesson(lesson); //Ищем добавленный урок

                        lessonOptions.setLessonId(lesson.getId()); ////Устанавливаем id перенесенного урока в опции
                        lessonOptions.setCalendarEventId(addCalendarEvent(course.getName() + " lesson", dateAndTime.getTimeInMillis(), timeEnd.getTimeInMillis())); //Устанавливаем ID события в календаре

                        //dbHelper.insertLessonOptions(lessonOptions); //Добавляем опции урока в БД
                    }
                    else{
                        dbHelper.insertLessonOptions(lessonOptions); //Добавляем опции урока в БД
                    }

                    //Проверяем, нужно ли предложить создать еще один урок
                    if(CURRENT_LESSON<LESSONS){

                        //Создаем новое намерение и отправляем вместе с ним данные
                        Intent intent = new Intent(NewLessonActivity.this, NewLessonActivity.class);
                        intent.putExtra("COURSE_ID", COURSE_ID);
                        intent.putExtra("LESSONS", LESSONS);
                        intent.putExtra("CURRENT_LESSON", CURRENT_LESSON);
                        intent.putExtra("COURSE_REPEAT", COURSE_REPEAT);
                        intent.putExtra("COURSE_REPEAT_MODE", COURSE_REPEAT_MODE);

                        finish();
                        startActivity(intent);
                        CustomIntent.customType(NewLessonActivity.this,"left-to-right");
                    }
                    else {
                        //Возврат на главный экран
                        Intent intent = new Intent(NewLessonActivity.this, MainActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(NewLessonActivity.this,"left-to-right");
                        finish();
                    }
                }
                else {
                    //Возврат на главный экран
                    Intent intent = new Intent(NewLessonActivity.this, MainActivity.class);
                    startActivity(intent);
                    CustomIntent.customType(NewLessonActivity.this,"left-to-right");
                    finish();
                }
            }
        });


    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    //отображаем диалоговое окно для выбора времени окончания
    public void setEndTime(View v) {
        new TimePickerDialog(this, te,
                timeEnd.get(Calendar.HOUR_OF_DAY),
                timeEnd.get(Calendar.MINUTE), true)
                .show();
    }


    // установка даты и времени в TextView
    private void setInitialDateTime() {

        //В TextView начала урока
        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));

        //В TextView конца урока
        endDateTime.setText(DateUtils.formatDateTime(this,
                timeEnd.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    TimePickerDialog.OnTimeSetListener te = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            timeEnd.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            timeEnd.set(Calendar.YEAR, year);
            timeEnd.set(Calendar.MONTH, monthOfYear);
            timeEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    //Добавление урока в системный календарь
    private int addCalendarEvent(String name,long startDate, long endDate){
        CalendarHelper calendarHelper = new CalendarHelper(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ALLOW_ADD_TO_CALENDAR = prefs.getBoolean("AllowAddToCalendar", false);
        if (ALLOW_ADD_TO_CALENDAR){
            return calendarHelper.addCalendarEvent(name, startDate, endDate);
        }
        else return -1;
    }
}