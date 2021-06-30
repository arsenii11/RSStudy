package com.example.finances.notifications;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import com.example.finances.calendar.CalendarHelper;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.database.LessonOptions;

import java.util.Calendar;

import static com.example.finances.MainActivity.ALLOW_ADD_TO_CALENDAR;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    static String ACTION;
    static String TITLE;
    static String TEXT;
    static String BIG_TEXT;
    static int LESSON_ID;

    @Override
    public boolean onStartJob(JobParameters params) {

        PersistableBundle bundle = params.getExtras();
        ACTION = bundle.getString("ACTION");
        TITLE = bundle.getString("TITLE");
        TEXT = bundle.getString("TEXT");
        BIG_TEXT = bundle.getString("BIG_TEXT");
        LESSON_ID = bundle.getInt("LESSON_ID");

        Log.e("JobSchedulerService", "Check");
        Log.e("JobSchedulerService", ACTION);
        Log.e("JobSchedulerService", TITLE);
        Log.e("JobSchedulerService", TEXT);
        Log.e("JobSchedulerService", BIG_TEXT);

        Context context = getApplicationContext();
        DBHelper dbHelper = new DBHelper(context);

        LessonOptions lessonOptions = dbHelper.getLessonOptions(LESSON_ID);

        if(lessonOptions.getIsRepeatable() > 0){
            Calendar calendar = Calendar.getInstance();
            Lesson mainLesson = dbHelper.getLesson(LESSON_ID);
            Course parentCourse = dbHelper.getCourse(mainLesson.getCourseId());
            Lesson lesson = new Lesson();
            lesson.setDuration(mainLesson.getDuration());
            lesson.setCourseId(mainLesson.getCourseId());
            lesson.setWeight(mainLesson.getWeight());

            String hours = String.valueOf(lesson.getDuration()).split("\\.")[0];
            String minutes = String.valueOf(lesson.getDuration()).split("\\.")[1];
            minutes = minutes.length() < 2 ? "0" + minutes : minutes;
            String dur = hours + ":" + minutes;

            long add = 0;
            switch (lessonOptions.getRepeatMode()){
                case 1: add = 604800000L; break;
                case 2: add = 1209600000L; break;
                case 3: add = 1814400000L; break;
            }

            calendar.setTimeInMillis(calendar.getTimeInMillis() + add);
            lesson.setDate(calendar.getTimeInMillis()/1000);

            String lessonName = parentCourse.getName() + ", " + DateUtils.formatDateTime(this,
                    calendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_SHOW_TIME) + ", " + dur + " hours";
            lesson.setName(lessonName);

            long lessonId = dbHelper.insertLessonSmart(lesson);

            lesson = dbHelper.getLesson(lessonId);

            LessonOptions options = new LessonOptions();
            options.setIsRepeatable(lessonOptions.getIsRepeatable());
            options.setRepeatMode(lessonOptions.getRepeatMode());
            options.setLessonId(lesson.getId());

            long endDate = lesson.getDate() + (long) lesson.getDuration()*3600000;
            options.setCalendarEventId(addCalendarEvent(parentCourse.getName() + " lesson", lesson.getDate(), endDate));
        }

        AlarmJobIntentService.startAction(context, ACTION, TITLE, TEXT, BIG_TEXT);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return true;
    }

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
