package com.example.finances.events.lesson;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

import static com.example.finances.MainActivity.ALLOW_ADD_TO_CALENDAR;


public class NewLessonActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    Calendar startCalendar; //Календарь начала
    Calendar endCalendar; //Календарь конца
    TextView startDate; //Строка с датой начала
    TextView startTime; //Строка с временем начала
    TextView endTime; //Строка со временем конца
    Button next; //Кнопка дальше
    int COURSE_ID; //ID родительского курса
    int LESSONS; //Количество уроков, которые нужно сейчас добавить
    int CURRENT_LESSON; //Текущее количество добавленных уроков
    String COURSE_REPEAT; //Повторяется ли урок
    String COURSE_REPEAT_MODE; //Режим повторения
    RadioGroup radioGroup; //Группа RadioButton для выбора режима повторения урока
    SwitchMaterial repeatOnOff; //Перключатель повтора урока

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_new);

        //Устанавливаем текущую дату и время в календари
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        //Получаем значения из intent
        Intent i = getIntent();
        COURSE_ID = i.getIntExtra("COURSE_ID", -1);
        LESSONS = i.getIntExtra("LESSONS", -1);
        CURRENT_LESSON = i.getIntExtra("CURRENT_LESSON", -1);

        startDate = findViewById(R.id.startDate); //Ищем TextView для даты начала
        startTime = findViewById(R.id.startTime); //Ищем TextView для времени начала
        endTime = findViewById(R.id.endTime); //Ищем TextView для времени конца


        repeatOnOff = findViewById(R.id.repeatSwitch); //ищем переключатель повтора on/off
        radioGroup = findViewById(R.id.radioGroup); //Ищем группу RadioButton для выбора режма повторения урока

        next = findViewById(R.id.buttonLessonNext); //Ищем кнопку дальше

        repeatOnOff.setOnCheckedChangeListener(this);

        //Назначаем действия при клике на кнопку дальше
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if ((startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) && !startTime.getText().toString().contains("__") && !endTime.getText().toString().contains("__")) {

                    //Определям режим повторения
                    if (repeatOnOff.isChecked())
                        COURSE_REPEAT = "YES";
                    else
                        COURSE_REPEAT = "NO";

                    //Ищем выбранный режим повтора
                    RadioButton modeRadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                    COURSE_REPEAT_MODE =  modeRadioButton.getText().toString().toUpperCase();

                    DBHelper dbHelper = new DBHelper(getApplicationContext()); //заполняем БД
                    Lesson lesson = new Lesson(); //Создаем пустой урок
                    Course course = dbHelper.getCourse(COURSE_ID); //Получаем родительский курс из БД по его ID

                    String currentTime = startTime.getText().toString(); //Получаем из TextView время начала урока
                    if (currentTime.split(" ").length > 1) {
                        if (currentTime.split(" ")[1].equals("AM")) {
                            currentTime = currentTime.split(" ")[0];
                            if (currentTime.split(":")[0].equals("12"))
                                currentTime = "0:" + currentTime.split(":")[1];
                        } else {
                            currentTime = currentTime.split(" ")[0];
                            int hours = Integer.parseInt(currentTime.split(":")[0]) + 12;
                            currentTime = String.valueOf(hours) + ":" + currentTime.split(":")[1];
                        }
                    }

                    String endTimeStr = endTime.getText().toString(); //Получаем из TextView время окончания урока
                    if (endTimeStr.split(" ").length > 1) {
                        if (endTimeStr.split(" ")[1].equals("AM")) {
                            endTimeStr = endTimeStr.split(" ")[0];
                            if (endTimeStr.split(":")[0].equals("12"))
                                endTimeStr = "0:" + endTimeStr.split(":")[1];
                        } else {
                            endTimeStr = endTimeStr.split(" ")[0];
                            int hours = Integer.parseInt(endTimeStr.split(":")[0]) + 12;
                            endTimeStr = String.valueOf(hours) + ":" + endTimeStr.split(":")[1];
                        }
                    }
                    //Рассчитываем длительность урока в формате HH:MM
                    String dur = NewLessonActivity.this.endTime.getText().toString();

                    int hours = Integer.parseInt(endTimeStr.split(":")[0]) - Integer.parseInt(currentTime.split(":")[0]);
                    float minutes = Float.parseFloat(endTimeStr.split(":")[1]) - Float.parseFloat(currentTime.split(":")[1]);

                    if (minutes < 0) {
                        hours--;
                        minutes += 60;
                    }

                    String hoursStr = String.valueOf(hours);
                    String minutesStr = String.valueOf(minutes).split("\\.")[0];
                    minutesStr = minutesStr.length() < 2 ? "0" + minutesStr : minutesStr;
                    dur = hoursStr + ":" + minutesStr;


                    String lessonName = course.getName() + ", " + startDate.getText().toString() + ", " + startTime.getText().toString() + ", " + dur + " hours"; //Вычисляем имя урока
                    lesson.setName(lessonName); //Устанавливаем имя урока

                    lesson.setCourseId(COURSE_ID); //устанавливаем уроку ID родительского курса

                    long dat = startCalendar.getTimeInMillis() / 1000; //Рассчитываем дату начала урока в секундах
                    lesson.setDate(dat); //Устанавливаем дату начала

                    //Рассчитываем и устанавливаем длительность урока в виде десятичной дроби
                    try {
                        lesson.setDuration(Integer.parseInt(dur.split(":")[0]) + Float.parseFloat(dur.split(":")[1]) / 60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //Запускаем умное добавление урока в БД
                    long lessonId = dbHelper.insertLessonSmart(lesson);
                    if (lessonId != -1) {

                        CURRENT_LESSON++; //Увеличиваем счетчик установленных уроков

                        LessonOptions lessonOptions = new LessonOptions(); //Создаем новые опции для урока

                        lesson = dbHelper.getLesson(lessonId); //Ищем добавленный урок

                        lessonOptions.setLessonId(lesson.getId()); //Устанавливаем id урока в опции
                        lessonOptions.setCalendarEventId(addCalendarEvent(course.getName() + " lesson", startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis())); //Устанавливаем ID события в календаре
                        lessonOptions.setIsRepeatable(0); //Ставим режим "не повторять"
                        lessonOptions.setRepeatMode(1); //Ставим повтор урока раз в неделю

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
                            } else if (COURSE_REPEAT_MODE.equals("EVERY TWO WEEKS")) {
                                add = 1209600000L;
                                lessonOptions.setRepeatMode(2);
                            }

                        }
                        dbHelper.insertLessonOptions(lessonOptions); //Добавляем опции урока в БД

                        //Проверяем, нужно ли предложить создать еще один урок
                        if (CURRENT_LESSON < LESSONS) {

                            //Создаем новое намерение и отправляем вместе с ним данные
                            Intent intent = new Intent(NewLessonActivity.this, NewLessonActivity.class);
                            intent.putExtra("COURSE_ID", COURSE_ID);
                            intent.putExtra("LESSONS", LESSONS);
                            intent.putExtra("CURRENT_LESSON", CURRENT_LESSON);
                            intent.putExtra("COURSE_REPEAT", COURSE_REPEAT);
                            intent.putExtra("COURSE_REPEAT_MODE", COURSE_REPEAT_MODE);

                            finish();
                            startActivity(intent);
                            CustomIntent.customType(NewLessonActivity.this, "left-to-right");
                        } else {
                            //Возврат на главный экран
                            Intent intent = new Intent(NewLessonActivity.this, MainActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(NewLessonActivity.this, "left-to-right");
                            finish();
                        }
                    } else {
                        //Возврат на главный экран
                        Intent intent = new Intent(NewLessonActivity.this, MainActivity.class);
                        startActivity(intent);
                        CustomIntent.customType(NewLessonActivity.this, "left-to-right");
                        finish();
                    }
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewLessonActivity.this);
                    builder.setTitle("Error!")
                            .setMessage("Please choose correct date and time")
                            .setCancelable(true)
                            .setNegativeButton("Ok", ((dialog, which) -> {
                                dialog.cancel();
                            }))
                            .create()
                            .show();
                }
            }
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
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCalendar.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    TimePickerDialog.OnTimeSetListener te = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endCalendar.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, monthOfYear);
            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, monthOfYear);
            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
        radioGroup.setVisibility(View.VISIBLE);}
        else{radioGroup.setVisibility(View.INVISIBLE);}
    }
}