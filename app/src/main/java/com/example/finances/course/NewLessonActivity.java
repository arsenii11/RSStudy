package com.example.finances.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;

import java.util.Calendar;
import java.util.TimeZone;

import maes.tech.intentanim.CustomIntent;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class NewLessonActivity extends AppCompatActivity {

    Calendar dateAndTime=Calendar.getInstance();
    TextView currentDateTime;
    EditText duration;
    Button next;
    int COURSE_ID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lesson);

        final String[] LessonInfo = new String[]{"Select time","Select date"};

        COURSE_ID = getIntent().getIntExtra("COURSE_ID", -1);
        currentDateTime=(TextView)findViewById(R.id.currentDateTime);
        duration = (EditText) findViewById(R.id.editTextLessonDuration);
        next = findViewById(R.id.buttonLessonNext);

        //Установка маски на ввод
        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("_:__");
        FormatWatcher formatWatcher = new MaskFormatWatcher(MaskImpl.createTerminated(slots));
        formatWatcher.installOn(duration);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                Lesson lesson = new Lesson();
                Course course = dbHelper.getCourse(COURSE_ID);
                String lessonName = course.getName() + ", " + currentDateTime.getText().toString() + ", " + duration.getText()+" hours";
                lesson.setName(lessonName);
                lesson.setCourseId(COURSE_ID);

                long dat = dateAndTime.getTimeInMillis()/1000;
                lesson.setDate(dat);

                String dur = duration.getText().toString();
                try {
                    lesson.setDuration(Integer.parseInt(dur.split(":")[0]) + Float.parseFloat(dur.split(":")[1]) / 60);
                } catch (Exception e){
                    e.printStackTrace();
                }

                dbHelper.insertLessonSmart(lesson);

                Intent intent = new Intent(NewLessonActivity.this, CourseActivity.class);
                intent.putExtra("COURSE_ID", COURSE_ID);
                startActivity(intent);
                CustomIntent.customType(NewLessonActivity.this,"left-to-right");
                finish();
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(this, "fadein-to-fadeout");
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        new TimePickerDialog(this,  t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    //Добавление урока в системный календарь
    public void addCalendarEvent(String name,long startDate, long endDate){
        Uri calendars = Uri.parse("content://com.android.calendar/calendars"); //Адрес существующих календарей
        Uri events = Uri.parse("content://com.android.calendar/events"); //Адрес событий в календарях

        //Создаем намерение запроса в БД системного календаря
        CursorLoader cursorLoader = new CursorLoader(this,
                calendars,
                new String[]{"_id"},
                null,
                null,
                null);
        Cursor cursor = cursorLoader.loadInBackground(); //Получаем указатель на ответ БД

        int calendarID = 0; //ID календаря

        //Проверяем не пустой ли ответ БД и ставим указатель в начало
        if (cursor.getCount() > 0 && cursor.moveToFirst())
        {
            //проходим по ответу БД и ищем ID последнего календаря
            while (!cursor.isAfterLast()) {
                calendarID = cursor.getInt(cursor.getColumnIndex("_id")); //Записываем данные из ответа БД в переменную
                cursor.moveToNext(); //Идем к следующей строке ответа
            }
            cursor.close(); //Закрываем запрос
        }

        ContentResolver contentResolver = getContentResolver(); //Создаем отправителя запроса
        ContentValues calendarEvent = new ContentValues(); //Создаем массив для отправляемых данных
        calendarEvent.put(CalendarContract.Events.CALENDAR_ID, calendarID); //ID календаря
        calendarEvent.put(CalendarContract.Events.TITLE, name); //Название события
        calendarEvent.put(CalendarContract.Events.DTSTART,startDate); //Дата начала события
        calendarEvent.put(CalendarContract.Events.DTEND, endDate); //Дата окончания
        calendarEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName()); //Временная зона (без неё не отправляется запрос)
        Uri uri = contentResolver.insert(events, calendarEvent); //Отправялем запрос
        //int id = Integer.parseInt(uri.getLastPathSegment()); //Получаем ID установленного события
    }
}