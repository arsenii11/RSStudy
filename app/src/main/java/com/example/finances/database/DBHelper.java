package com.example.finances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
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

    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+1);
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

    public boolean deleteCourse(Course course){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_COURSES, KEY_COURSE_ID+" = "+ course.getId(), null) == -1)
            status = false;

        return status;
    }

    public boolean deleteCourse(int courseId){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_COURSES, KEY_COURSE_ID+" = "+ courseId, null) == -1)
            status = false;

        return status;
    }

    public ArrayList<Course> getAllCourses(){
        ArrayList<Course> arrayList = new ArrayList<Course>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_COURSES, null);
        cursor.moveToFirst();

        while (!cursor.isLast()){
            Course course = new Course();

            course.setId(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID)));
            course.setName(cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME)));
            course.setStartDate(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_START_DATE)));
            course.setEndDate(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_END_DATE)));
            course.setFinished(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED)));
            course.setLessons(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS)));
            course.setLessonsCompleted(cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS)));

            arrayList.add(course);
            Log.println(Log.ERROR, "COURSE", course.getName());
            cursor.moveToNext();
        }

        return arrayList;
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

    public boolean updateLesson(Lesson oldLesson, Lesson newLesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, newLesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, newLesson.getCourseId());
        cv.put(KEY_LESSON_DATE, newLesson.getDate());
        cv.put(KEY_LESSON_DURATION, newLesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, newLesson.getWeight());

        if(db.update(TABLE_LESSONS, cv, KEY_LESSON_ID+" = "+oldLesson.getId(), null) == -1)
            status = false;

        return status;
    }

    public boolean updateLesson(int oldLessonId, Lesson newLesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_LESSON_NAME, newLesson.getName());
        cv.put(KEY_LESSON_COURSE_ID, newLesson.getCourseId());
        cv.put(KEY_LESSON_DATE, newLesson.getDate());
        cv.put(KEY_LESSON_DURATION, newLesson.getDuration());
        cv.put(KEY_LESSON_WEIGHT, newLesson.getWeight());

        if(db.update(TABLE_LESSONS, cv, KEY_LESSON_ID+" = "+oldLessonId, null) == -1)
            status = false;

        return status;
    }

    public boolean deleteLesson(Lesson lesson){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_LESSONS, KEY_LESSON_ID+" = "+ lesson.getId(), null) == -1)
            status = false;

        return status;
    }

    public boolean deleteLesson(int lessonId){
        boolean status = true;
        SQLiteDatabase db = this.getWritableDatabase();

        if(db.delete(TABLE_LESSONS, KEY_LESSON_ID+" = "+ lessonId, null) == -1)
            status = false;

        return status;
    }

    public ArrayList<Lesson> getAllLessons(){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS, null);
        cursor.moveToFirst();

        while (!cursor.isLast()){
            Lesson lesson = new Lesson();

            lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));

            arrayList.add(lesson);

            cursor.moveToNext();
        }

        return arrayList;
    }

    public ArrayList<Lesson> getAllLessons(Course course){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS+" where "+KEY_LESSON_ID+" = "+course.getId(), null);
        cursor.moveToFirst();

        while (!cursor.isLast()){
            Lesson lesson = new Lesson();

            lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));

            arrayList.add(lesson);

            cursor.moveToNext();
        }

        return arrayList;
    }

    public ArrayList<Lesson> getAllLessons(int courseId){
        ArrayList<Lesson> arrayList = new ArrayList<Lesson>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_LESSONS+" where "+KEY_LESSON_ID+" = "+courseId, null);
        cursor.moveToFirst();

        while (!cursor.isLast()){
            Lesson lesson = new Lesson();

            lesson.setId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID)));
            lesson.setName(cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME)));
            lesson.setCourseId(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID)));
            lesson.setDate(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DATE)));
            lesson.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_DURATION)));
            lesson.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT)));

            arrayList.add(lesson);

            cursor.moveToNext();
        }

        return arrayList;
    }
}
