package com.example.finances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RSSTUDIO.db";

    public static final String TABLE_COURSES = "Courses";
    public static final String KEY_COURSE_ID = "_id";
    public static final String KEY_COURSE_NAME = "name";
    public static final String KEY_COURSE_START_DATE = "startDate";
    public static final String KEY_COURSE_END_DATE = "endDate";
    public static final String KEY_COURSE_FINISHED = "finished";
    public static final String KEY_COURSE_LESSONS = "lessons";
    public static final String KEY_COURSE_COMPLETED_LESSONS = "lessonsCompleted";

    public static final String TABLE_LESSONS = "Lessons";
    public static final String KEY_LESSON_ID = "_id";
    public static final String KEY_LESSON_NAME = "name";
    public static final String KEY_LESSON_COURSE_ID = "courseId";
    public static final String KEY_LESSON_DATE = "date";
    public static final String KEY_LESSON_DURATION = "duration";
    public static final String KEY_LESSON_WEIGHT = "weight";
    public static final String KEY_LESSON_CALENDAR_EVENT_ID = "calendarEventId";

    public static final String TABLE_TESTS = "Tests";
    public static final String KEY_TEST_ID = "_id";
    public static final String KEY_TEST_NAME = "name";
    public static final String KEY_TEST_COURSE_ID = "courseId";
    public static final String KEY_TEST_DATE = "date";
    public static final String KEY_TEST_WEIGHT = "weight";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    //Создание БД
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " + TABLE_COURSES +"(" + KEY_COURSE_ID + " integer primary key, " + KEY_COURSE_NAME +" text, " + KEY_COURSE_START_DATE+ " integer, " + KEY_COURSE_END_DATE + " integer, " + KEY_COURSE_FINISHED + " integer, " + KEY_COURSE_LESSONS + " integer, " + KEY_COURSE_COMPLETED_LESSONS + " integer " + ")");
        db.execSQL(" create table " + TABLE_LESSONS +"(" + KEY_LESSON_ID + " integer primary key, " + KEY_LESSON_NAME +" text, " + KEY_LESSON_COURSE_ID + " integer, " + KEY_LESSON_DATE + " text, " + KEY_LESSON_DURATION + " real, " + KEY_LESSON_WEIGHT + " integer, " + KEY_LESSON_CALENDAR_EVENT_ID + " integer " + ")");
        db.execSQL(" create table " + TABLE_TESTS + "(" + KEY_TEST_ID + " integer primary key, " + KEY_TEST_NAME +" text, " + KEY_TEST_COURSE_ID + " integer, " + KEY_TEST_DATE + " text, " + KEY_TEST_WEIGHT + " integer " + ")");
    }

    //Обновление версии БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TABLE_COURSES);
        db.execSQL("drop table if exists "+ TABLE_LESSONS);
        db.execSQL("drop table if exists "+ TABLE_TESTS);
        onCreate(db);
    }

    //Очистить БД
    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+1);
    }

    //COURSE
    //Добавить курс
    public boolean insertCourse(Course course){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_COURSE_NAME, course.getName());
        cv.put(KEY_COURSE_START_DATE, course.getStartDate());
        cv.put(KEY_COURSE_END_DATE, course.getEndDate());
        cv.put(KEY_COURSE_FINISHED, course.getFinished());
        cv.put(KEY_COURSE_LESSONS, course.getLessons());
        cv.put(KEY_COURSE_COMPLETED_LESSONS, course.getLessonsCompleted());

        if(db.insert(TABLE_COURSES, null, cv) == -1)
            status = false;

        return status;
    }

    //Обновить существующий курс
    public boolean updateCourse(Course oldCourse, Course newCourse){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_COURSE_NAME, newCourse.getName());
        cv.put(KEY_COURSE_START_DATE, newCourse.getStartDate());
        cv.put(KEY_COURSE_END_DATE, newCourse.getEndDate());
        cv.put(KEY_COURSE_FINISHED, newCourse.getFinished());
        cv.put(KEY_COURSE_LESSONS, newCourse.getLessons());
        cv.put(KEY_COURSE_COMPLETED_LESSONS, newCourse.getLessonsCompleted());

        if(db.update(TABLE_COURSES, cv, KEY_COURSE_ID+" = "+ oldCourse.getId(), null) == -1)
            status = false;

        return status;
    }

    //Обновить существующий курс
    public boolean updateCourse(int oldCourseId, Course newCourse){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_COURSE_NAME, newCourse.getName());
        cv.put(KEY_COURSE_START_DATE, newCourse.getStartDate());
        cv.put(KEY_COURSE_END_DATE, newCourse.getEndDate());
        cv.put(KEY_COURSE_FINISHED, newCourse.getFinished());
        cv.put(KEY_COURSE_LESSONS, newCourse.getLessons());
        cv.put(KEY_COURSE_COMPLETED_LESSONS, newCourse.getLessonsCompleted());

        if(db.update(TABLE_COURSES, cv, KEY_COURSE_ID+" = "+ oldCourseId, null) == -1)
            status = false;

        return status;
    }

    //Удалить курс
    public boolean deleteCourse(Course course){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_COURSES, KEY_COURSE_ID+" = "+ course.getId(), null) == -1)
            status = false;

        for (Lesson lesson: getAllLessons(course.getId())) {
            if(!deleteLesson(lesson)) status = false;
        }

        for (Test test: getAllTests(course.getId())){
            if(!deleteTest(test)) status = false;
        }

        return status;
    }

    //Удалить курс
    public boolean deleteCourse(int courseId){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_COURSES, KEY_COURSE_ID+" = "+ courseId, null) == -1)
            status = false;

        for (Lesson lesson: getAllLessons(courseId)) {
            if(!deleteLesson(lesson)) status = false;
        }

        for (Test test: getAllTests(courseId)){
            if(!deleteTest(test)) status = false;
        }

        return status;
    }

    //Получить все курсы из БД
    public ArrayList<Course> getAllCourses(){
        ArrayList<Course> arrayList = new ArrayList<Course>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_COURSES, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Course course = new Course();

                course.setId(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)));
                course.setName(cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME)));
                course.setStartDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE)));
                course.setEndDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE)));
                course.setFinished(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED)));
                course.setLessons(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS)));
                course.setLessonsCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS)));

                arrayList.add(course);

                cursor.moveToNext();
            }
        }

        cursor.close();

        return arrayList;
    }

    //Получить курс по ID
    public Course getCourse(int courseId){
        Course course = new Course();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_COURSES+" where "+KEY_COURSE_ID+"="+courseId, null);
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            course.setId(courseId);
            course.setName(cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME)));
            course.setStartDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE)));
            course.setEndDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE)));
            course.setFinished(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED)));
            course.setLessons(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS)));
            course.setLessonsCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS)));
        }
        cursor.close();
        return  course;
    }

    //Найти курс
    public Course findCourse(Course course){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_COURSES+" where "+KEY_COURSE_NAME+"='"+course.getName()+"' and "+KEY_COURSE_LESSONS+"="+course.getLessons(), null);
        cursor.moveToFirst();
        if (cursor.isLast()) {
            course.setId(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)));
            course.setName(cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME)));
            course.setStartDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE)));
            course.setEndDate(cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE)));
            course.setFinished(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED)));
            course.setLessons(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS)));
            course.setLessonsCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS)));
        }
        cursor.close();
        return course;

    }

    //LESSON
    //Добавить урок
    public boolean insertLesson(Lesson lesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, lesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, lesson.getCourseId());
        cv.put(KEY_LESSON_DATE, lesson.getDate());
        cv.put(KEY_LESSON_DURATION, lesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, lesson.getWeight());
        cv.put(KEY_LESSON_CALENDAR_EVENT_ID, lesson.getCalendarEventId());

        if(db.insert(TABLE_LESSONS, null, cv) == -1)
            status = false;

        return status;
    }

    //Умное добавление урока (изменяется дата начала родительского курса)
    public boolean insertLessonSmart(Lesson lesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        Course parentCourse = getCourse(lesson.getCourseId());
        if ((parentCourse.getStartDate() > lesson.getDate()) || parentCourse.getStartDate() <= 0) {
            parentCourse.setStartDate(lesson.getDate());
            updateCourse(parentCourse.getId(), parentCourse);
        }
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, lesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, lesson.getCourseId());
        cv.put(KEY_LESSON_DATE, lesson.getDate());
        cv.put(KEY_LESSON_DURATION, lesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, lesson.getWeight());
        cv.put(KEY_LESSON_CALENDAR_EVENT_ID, lesson.getCalendarEventId());

        if(db.insert(TABLE_LESSONS, null, cv) == -1)
            status = false;

        return status;
    }

    //Обновить существующий урок
    public boolean updateLesson(Lesson oldLesson, Lesson newLesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, newLesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, newLesson.getCourseId());
        cv.put(KEY_LESSON_DATE, newLesson.getDate());
        cv.put(KEY_LESSON_DURATION, newLesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, newLesson.getWeight());
        cv.put(KEY_LESSON_CALENDAR_EVENT_ID, newLesson.getCalendarEventId());

        if(db.update(TABLE_LESSONS, cv, KEY_LESSON_ID+" = "+oldLesson.getId(), null) == -1)
            status = false;

        return status;
    }

    //Обновить существующий урок
    public boolean updateLesson(int oldLessonId, Lesson newLesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, newLesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, newLesson.getCourseId());
        cv.put(KEY_LESSON_DATE, newLesson.getDate());
        cv.put(KEY_LESSON_DURATION, newLesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, newLesson.getWeight());
        cv.put(KEY_LESSON_CALENDAR_EVENT_ID, newLesson.getCalendarEventId());

        if(db.update(TABLE_LESSONS, cv, KEY_LESSON_ID+" = "+oldLessonId, null) == -1)
            status = false;

        return status;
    }

    //удалить урок
    public boolean deleteLesson(Lesson lesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_LESSONS, KEY_LESSON_ID+" = "+ lesson.getId(), null) == -1)
            status = false;


        return status;
    }

    //Удалить урок
    public boolean deleteLesson(int lessonId){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_LESSONS, KEY_LESSON_ID+" = "+ lessonId, null) == -1)
            status = false;

        return status;
    }

    //Получить все уроки из БД
    public ArrayList<Lesson> getAllLessons(){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
                lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
                lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
                lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
                lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
                lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));

                arrayList.add(lesson);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все уроки из БД с одинаковым родительским курсом
    public ArrayList<Lesson> getAllLessons(Course course){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS+" where "+KEY_LESSON_COURSE_ID+" = "+course.getId(), null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
                lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
                lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
                lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
                lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
                lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));

                arrayList.add(lesson);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все уроки из БД с одинаковым родительским курсом
    public ArrayList<Lesson> getAllLessons(int courseId){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS+" where "+KEY_LESSON_COURSE_ID+" = "+courseId, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
                lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
                lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
                lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
                lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
                lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));

                arrayList.add(lesson);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все курсы за сегодняшний дней, отсортированные по времени
    public ArrayList<Lesson> getLessonsTodaySortByTime(){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        long dayTime = today.getTimeInMillis()/1000;
        long tomorrow = dayTime+86400;
        Cursor cursor = db.rawQuery("select * from " + TABLE_LESSONS + " where " + KEY_LESSON_DATE + " between " + dayTime + " and " + tomorrow + " order by " + KEY_LESSON_DATE, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
                lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
                lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
                lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
                lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
                lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));

                arrayList.add(lesson);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все уроки за определенный день, отсортированные по времени
    public ArrayList<Lesson> getLessonsFromDaySortByTime(long datetime){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(datetime);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long dayTime = today.getTimeInMillis()/1000;
        long tomorrowTime = dayTime+86400;
        Cursor cursor = db.rawQuery("select * from " + TABLE_LESSONS + " where " + KEY_LESSON_DATE + " between " + dayTime + " and " + tomorrowTime + " order by " + KEY_LESSON_DATE, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
                lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
                lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
                lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
                lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
                lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));

                arrayList.add(lesson);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все уроки за сегодняшний день, отсортированные по весу
    public Lesson getLessonTodaySortByWeight(){
        Lesson lesson = new Lesson();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        long dayTime = today.getTimeInMillis()/1000;
        Cursor cursor = db.rawQuery("select * from " + TABLE_LESSONS + " where " + KEY_LESSON_DATE + ">=" + dayTime + " order by " + KEY_LESSON_WEIGHT, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
            lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));
        }
        cursor.close();
        return lesson;
    }

    //Получить ближайший к текущему времени урок
    public Lesson getLessonFromNowSortByTime(){
        Lesson lesson = new Lesson();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        long dayTime = today.getTimeInMillis()/1000;
        Cursor cursor = db.rawQuery("select * from " + TABLE_LESSONS + " where " + KEY_LESSON_DATE + ">=" + dayTime + " order by " + KEY_LESSON_DATE, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
            lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));
        }
        cursor.close();
        return lesson;
    }

    //Получить урок по ID
    public Lesson getLesson(int lessonId){
        Lesson lesson = new Lesson();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS+" where "+KEY_LESSON_ID+" = "+lessonId, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            lesson.setId(lessonId);
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));
            lesson.setCalendarEventId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_CALENDAR_EVENT_ID)));
        }
        cursor.close();
        return lesson;
    }

    //Получить длительность уроков за промежуток времени
    public float getLessonDurationInTime(long startDate, long endDate){
        float sum = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        startDate /= 1000;
        endDate /= 1000;
        Cursor cursor = db.rawQuery("select SUM("+KEY_LESSON_DURATION+") from " + TABLE_LESSONS + " where " + KEY_LESSON_DATE + " between " + startDate + " and " + endDate, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            sum = cursor.getFloat(0);
        }
        cursor.close();
        return sum;
    }


    //TEST
    //Добавить тест
    public boolean insertTest(Test test){
        boolean status = true;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TEST_NAME, test.getName());
        cv.put(KEY_TEST_COURSE_ID, test.getCourseId());
        cv.put(KEY_TEST_DATE, test.getDate());
        cv.put(KEY_TEST_WEIGHT, test.getWeight());

        if(db.insert(TABLE_TESTS, null, cv) == -1)
            status = false;

        return status;
    }

    //Обновить существующий тест
    public boolean updateTest(Test oldTest, Test test){
        boolean status = true;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TEST_NAME, test.getName());
        cv.put(KEY_TEST_COURSE_ID, test.getCourseId());
        cv.put(KEY_TEST_DATE, test.getDate());
        cv.put(KEY_TEST_WEIGHT, test.getWeight());

        if(db.update(TABLE_TESTS, cv, KEY_TEST_ID + " = "+ oldTest.getId(), null) == -1)
            status = false;

        return status;
    }

    //Обновить существующий тест
    public boolean updateTest(int testId, Test test){
        boolean status = true;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TEST_NAME, test.getName());
        cv.put(KEY_TEST_COURSE_ID, test.getCourseId());
        cv.put(KEY_TEST_DATE, test.getDate());
        cv.put(KEY_TEST_WEIGHT, test.getWeight());

        if(db.update(TABLE_TESTS, cv, KEY_TEST_ID + " = "+ testId, null) == -1)
            status = false;

        return status;
    }

    //Удалить тест
    public boolean deleteTest(Test test){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_TESTS, KEY_TEST_ID+" = "+ test.getId(), null) == -1)
            status = false;

        return status;
    }

    //Удалить тест
    public boolean deleteTest(int testId){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_TESTS, KEY_TEST_ID+" = "+ testId, null) == -1)
            status = false;

        return status;
    }

    //Получить все тесты
    public ArrayList<Test> getAllTests(){
        ArrayList<Test> arrayList = new ArrayList<Test>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_TESTS, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Test test = new Test();

                test.setId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID)));
                test.setName(cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME)));
                test.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID)));
                test.setDate(cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE)));
                test.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT)));

                arrayList.add(test);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все тесты с одинаковым родительским курсом
    public ArrayList<Test> getAllTests(int courseId){
        ArrayList<Test> arrayList = new ArrayList<Test>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_TESTS+" where "+KEY_TEST_COURSE_ID+" = "+courseId, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Test test = new Test();

                test.setId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID)));
                test.setName(cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME)));
                test.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID)));
                test.setDate(cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE)));
                test.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT)));

                arrayList.add(test);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    //Получить все тесты за определенный день, отсортированные по времени
    public ArrayList<Test> getTestsFromDaySortByTime(long datetime){
        ArrayList<Test> arrayList = new ArrayList<Test>();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(datetime);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long dayTime = today.getTimeInMillis()/1000;
        long tomorrowTime = dayTime+86400;
        Cursor cursor = db.rawQuery("select * from " + TABLE_TESTS + " where " + KEY_TEST_DATE + " between " + dayTime + " and " + tomorrowTime + " order by " + KEY_TEST_DATE, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Test test = new Test();

                test.setId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID)));
                test.setName(cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME)));
                test.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID)));
                test.setDate(cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE)));
                test.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT)));

                arrayList.add(test);

                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;

    }

    //Получить ближаший к текущему времени тест
    public Test getTestFromNowSortByTime(){
        Test test = new Test();
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar today = Calendar.getInstance();
        long dayTime = today.getTimeInMillis()/1000;
        Cursor cursor = db.rawQuery("select * from " + TABLE_TESTS + " where " + KEY_TEST_DATE + ">=" + dayTime + " order by " + KEY_TEST_DATE, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            test.setId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID)));
            test.setName(cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME)));
            test.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID)));
            test.setDate(cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE)));
            test.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT)));
        }
        cursor.close();
        return test;
    }

    //TOTAL
    public Object getEventFromNowSortByTime(){
        Lesson lesson = getLessonFromNowSortByTime();
        Test test = getTestFromNowSortByTime();
        if (test.getName() == null) return lesson;
        else if(lesson.getName() == null) return test;
        else if (test.getDate()<=lesson.getDate()) return test;
        else return lesson;
    }

    //Получить название ближайшего к текущему времени события (урока или теста)
    public String getEventFromNowSortByTimeStr(){
        String str = null;
        Calendar calendar = Calendar.getInstance();
        Lesson lesson = getLessonFromNowSortByTime();
        Test test = getTestFromNowSortByTime();
        if (test.getName() == null || (test.getDate()>=lesson.getDate())) {
            //calendar.setTimeInMillis(lesson.getDate()*1000);
            //str = lesson.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
            String[] names = lesson.getName().split(", ");
            str = names[0] + " lesson at " + names[2] + " " + names[1];
        }
        else if((lesson.getName() == null) || (test.getDate()<=lesson.getDate())) {
            //calendar.setTimeInMillis(test.getDate()*1000);
            //str = test.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
            String[] names = test.getName().split(", ");
            str = names[0] + " at " + names[2] + " " + names[1];
        }
        return str;
    }

    //Получить названия всех событий (уроков или тестов) за определенный день, отсортированных по времени
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getEventFromDaySortByTime(long datetime){
        ArrayList<String> array = new ArrayList<String>();
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<Lesson> lessons = getLessonsFromDaySortByTime(datetime);
        ArrayList<Test> tests = getTestsFromDaySortByTime(datetime);

        lessons.forEach(lesson -> events.add(new Event(lesson.getId(), lesson.getName(), lesson.getCourseId(), lesson.getDate(), Event.EventType.Lesson)));
        tests.forEach(test -> events.add(new Event(test.getId(), test.getName(), test.getCourseId(), test.getDate(), Event.EventType.Test)));

        events.sort(((o1, o2) -> Long.compare(o1.getDate(), o2.getDate())));

        events.forEach(event -> array.add(event.getName()));
        return array;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<String> getAllEvents(int courseId){
        ArrayList<String> array = new ArrayList<String>();
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<Lesson> lessons = getAllLessons(courseId);
        ArrayList<Test> tests = getAllTests(courseId);

        lessons.forEach(lesson -> events.add(new Event(lesson.getId(), lesson.getName(), lesson.getCourseId(), lesson.getDate(), Event.EventType.Lesson)));
        tests.forEach(test -> events.add(new Event(test.getId(), test.getName(), test.getCourseId(), test.getDate(), Event.EventType.Test)));

        events.forEach(event -> array.add(event.getName()));
        return array;
    }

}
