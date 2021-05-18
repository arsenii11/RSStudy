package com.example.finances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RSSTUDIO.db";

    public static final String TABLE_COURSES = "Courses";

    public static final String KEY_COURSE_ID = "id";
    public static final String KEY_COURSE_NAME = "name";
    public static final String KEY_COURSE_START_DATE = "startDate";
    public static final String KEY_COURSE_END_DATE = "endDate";
    public static final String KEY_COURSE_FINISHED = "finished";
    public static final String KEY_COURSE_LESSONS = "lessons";
    public static final String KEY_COURSE_COMPLETED_LESSONS = "lessonsCompleted";

    public static final String TABLE_LESSONS = "Lessons";

    public static final String KEY_LESSON_ID = "id";
    public static final String KEY_LESSON_NAME = "name";
    public static final String KEY_LESSON_COURSE_ID = "courseId";
    public static final String KEY_LESSON_DATE = "date";
    public static final String KEY_LESSON_DURATION = "duration";
    public static final String KEY_LESSON_WEIGHT = "weight";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" create table " + TABLE_COURSES+"("+KEY_COURSE_ID+ " integer primary key ," + KEY_COURSE_NAME +" text, "+ KEY_COURSE_START_DATE+ " integer, "+ KEY_COURSE_END_DATE+ " integer, "+ KEY_COURSE_FINISHED+ " integer, "+ KEY_COURSE_LESSONS+ " integer, "+ KEY_COURSE_COMPLETED_LESSONS+ " integer "+ ")");
        db.execSQL(" create table " + TABLE_LESSONS+"("+KEY_LESSON_ID+ " integer primary key ," + KEY_LESSON_NAME +" text, "+ KEY_LESSON_COURSE_ID+ " integer, "+ KEY_LESSON_DATE+ " integer, "+ KEY_LESSON_DURATION+ " integer, "+ KEY_LESSON_WEIGHT+ " integer "+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TABLE_COURSES);
        db.execSQL("drop table if exists "+ TABLE_LESSONS);
        onCreate(db);
    }

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

    public boolean insertLesson(Lesson lesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, lesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, lesson.getCourseId());
        cv.put(KEY_LESSON_DATE, lesson.getDate());
        cv.put(KEY_LESSON_DURATION, lesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, lesson.getWeight());

        if(db.insert(TABLE_LESSONS, null, cv) == -1)
            status = false;

        return status;
    }
}
