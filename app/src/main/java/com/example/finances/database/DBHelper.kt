package com.example.finances.database

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import androidx.annotation.RequiresApi
import android.os.Build
import java.util.*
import java.util.function.Consumer

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    //Создание БД
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(" create table $TABLE_COURSES($KEY_COURSE_ID integer primary key, $KEY_COURSE_NAME text, $KEY_COURSE_START_DATE integer, $KEY_COURSE_END_DATE integer, $KEY_COURSE_FINISHED integer, $KEY_COURSE_LESSONS integer, $KEY_COURSE_COMPLETED_LESSONS integer )")
        db.execSQL(" create table $TABLE_LESSONS($KEY_LESSON_ID integer primary key, $KEY_LESSON_NAME text, $KEY_LESSON_COURSE_ID integer, $KEY_LESSON_DATE text, $KEY_LESSON_DURATION real, $KEY_LESSON_WEIGHT integer )")
        db.execSQL(" create table $TABLE_LESSON_OPTIONS($KEY_LESSON_OPTIONS_ID integer primary key, $KEY_LESSON_OPTIONS_LESSON_ID integer, $KEY_LESSON_OPTIONS_CALENDAR_EVENT_ID integer, $KEY_LESSON_OPTIONS_IS_REPEATABLE integer, $KEY_LESSON_OPTIONS_REPEAT_MODE integer, $KEY_LESSON_OPTIONS_DESCRIPTION text)")
        db.execSQL(" create table $TABLE_TESTS($KEY_TEST_ID integer primary key, $KEY_TEST_NAME text, $KEY_TEST_COURSE_ID integer, $KEY_TEST_DATE text, $KEY_TEST_WEIGHT integer )")
    }

    //Обновление версии БД
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE_COURSES")
        db.execSQL("drop table if exists $TABLE_LESSONS")
        db.execSQL("drop table if exists $TABLE_TESTS")
        onCreate(db)
    }

    //Очистить БД
    fun clear() {
        val db = this.writableDatabase
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION + 1)
    }

    //COURSE
    //Добавить курс
    fun insertCourse(course: Course): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_COURSE_NAME, course.name)
        cv.put(KEY_COURSE_START_DATE, course.startDate)
        cv.put(KEY_COURSE_END_DATE, course.endDate)
        cv.put(KEY_COURSE_FINISHED, course.finished)
        cv.put(KEY_COURSE_LESSONS, course.lessons)
        cv.put(KEY_COURSE_COMPLETED_LESSONS, course.lessonsCompleted)
        return db.insert(TABLE_COURSES, null, cv)
    }

    //Обновить существующий курс
    fun updateCourse(oldCourse: Course, newCourse: Course): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_COURSE_NAME, newCourse.name)
        cv.put(KEY_COURSE_START_DATE, newCourse.startDate)
        cv.put(KEY_COURSE_END_DATE, newCourse.endDate)
        cv.put(KEY_COURSE_FINISHED, newCourse.finished)
        cv.put(KEY_COURSE_LESSONS, newCourse.lessons)
        cv.put(KEY_COURSE_COMPLETED_LESSONS, newCourse.lessonsCompleted)
        if (db.update(TABLE_COURSES, cv, KEY_COURSE_ID + " = " + oldCourse.id, null) == -1) status =
            false
        return status
    }

    //Обновить существующий курс
    fun updateCourse(oldCourseId: Int, newCourse: Course): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_COURSE_NAME, newCourse.name)
        cv.put(KEY_COURSE_START_DATE, newCourse.startDate)
        cv.put(KEY_COURSE_END_DATE, newCourse.endDate)
        cv.put(KEY_COURSE_FINISHED, newCourse.finished)
        cv.put(KEY_COURSE_LESSONS, newCourse.lessons)
        cv.put(KEY_COURSE_COMPLETED_LESSONS, newCourse.lessonsCompleted)
        if (db.update(TABLE_COURSES, cv, "$KEY_COURSE_ID = $oldCourseId", null) == -1) status =
            false
        return status
    }

    //Удалить курс
    fun deleteCourse(course: Course): Boolean {
        var status = true
        val db = this.writableDatabase
        if (db.delete(TABLE_COURSES, KEY_COURSE_ID + " = " + course.id, null) == -1) status = false
        for (lesson in getAllLessons(course.id)) {
            if (!deleteLesson(lesson)) status = false
        }
        for (test in getAllTests(course.id)) {
            if (!deleteTest(test)) status = false
        }
        return status
    }

    //Удалить курс
    fun deleteCourse(courseId: Int): Boolean {
        var status = true
        val db = this.writableDatabase
        if (db.delete(TABLE_COURSES, "$KEY_COURSE_ID = $courseId", null) == -1) status = false
        for (lesson in getAllLessons(courseId)) {
            if (!deleteLesson(lesson)) status = false
        }
        for (test in getAllTests(courseId)) {
            if (!deleteTest(test)) status = false
        }
        return status
    }

    //Получить все курсы из БД
    val allCourses: ArrayList<Course>
        get() {
            val arrayList = ArrayList<Course>()
            val db = this.readableDatabase
            val cursor = db.rawQuery("select * from $TABLE_COURSES", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val course = Course()
                    course.id = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID))
                    course.name = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME))
                    course.startDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE))
                    course.endDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE))
                    course.finished = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED))
                    course.lessons = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS))
                    course.lessonsCompleted = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_COURSE_COMPLETED_LESSONS
                        )
                    )
                    arrayList.add(course)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить все активные курсы из БД
    val allActiveCourses: ArrayList<Course>
        get() {
            val arrayList = ArrayList<Course>()
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "select * from $TABLE_COURSES where $KEY_COURSE_FINISHED <= 0",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val course = Course()
                    course.id = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID))
                    course.name = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME))
                    course.startDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE))
                    course.endDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE))
                    course.finished = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED))
                    course.lessons = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS))
                    course.lessonsCompleted = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_COURSE_COMPLETED_LESSONS
                        )
                    )
                    arrayList.add(course)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить все закрытые курсы из БД
    val allFinishedCourses: ArrayList<Course>
        get() {
            val arrayList = ArrayList<Course>()
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "select * from $TABLE_COURSES where $KEY_COURSE_FINISHED > 0",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val course = Course()
                    course.id = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID))
                    course.name = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME))
                    course.startDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE))
                    course.endDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE))
                    course.finished = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED))
                    course.lessons = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS))
                    course.lessonsCompleted = cursor.getInt(
                        cursor.getColumnIndex(
                            KEY_COURSE_COMPLETED_LESSONS
                        )
                    )
                    arrayList.add(course)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить курс по ID
    fun getCourse(courseId: Long): Course {
        val course = Course()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_COURSES where $KEY_COURSE_ID=$courseId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            course.id = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID))
            course.name = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME))
            course.startDate =
                cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE))
            course.endDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE))
            course.finished = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED))
            course.lessons = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS))
            course.lessonsCompleted =
                cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS))
        }
        cursor.close()
        return course
    }

    val coursesSortByLessons: ArrayList<Course>
        get() {
            val courses = ArrayList<Course>()
            val db = this.readableDatabase
            val cursor = db.rawQuery(
                "select * from $TABLE_COURSES order by $KEY_COURSE_LESSONS desc",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val course = Course()
                    course.id = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_ID))
                    course.name = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME))
                    course.startDate =
                        cursor.getLong(cursor.getColumnIndex(KEY_COURSE_START_DATE))
                    course.endDate = cursor.getLong(cursor.getColumnIndex(KEY_COURSE_END_DATE))
                    course.finished = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_FINISHED))
                    course.lessons = cursor.getInt(cursor.getColumnIndex(KEY_COURSE_LESSONS))
                    course.lessonsCompleted =
                        cursor.getInt(cursor.getColumnIndex(KEY_COURSE_COMPLETED_LESSONS))
                    courses.add(course)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return courses
        }

    //LESSON
    //Добавить урок
    fun insertLesson(lesson: Lesson): Long {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LESSON_NAME, lesson.name)
        cv.put(KEY_LESSON_COURSE_ID, lesson.courseId)
        cv.put(KEY_LESSON_DATE, lesson.date)
        cv.put(KEY_LESSON_DURATION, lesson.duration)
        cv.put(KEY_LESSON_WEIGHT, lesson.weight)
        return db.insert(TABLE_LESSONS, null, cv)
    }

    //Умное добавление урока (изменяется дата начала родительского курса)
    fun insertLessonSmart(lesson: Lesson): Long {
        val db = this.writableDatabase
        val parentCourse = getCourse(lesson.courseId.toLong())
        parentCourse.lessons = parentCourse.lessons + 1
        if (parentCourse.startDate > lesson.date || parentCourse.startDate <= 0) {
            parentCourse.startDate = lesson.date
            updateCourse(parentCourse.id, parentCourse)
        }
        val cv = ContentValues()
        cv.put(KEY_LESSON_NAME, lesson.name)
        cv.put(KEY_LESSON_COURSE_ID, lesson.courseId)
        cv.put(KEY_LESSON_DATE, lesson.date)
        cv.put(KEY_LESSON_DURATION, lesson.duration)
        cv.put(KEY_LESSON_WEIGHT, lesson.weight)
        return db.insert(TABLE_LESSONS, null, cv)
    }

    //Обновить существующий урок
    fun updateLesson(oldLesson: Lesson, newLesson: Lesson): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LESSON_NAME, newLesson.name)
        cv.put(KEY_LESSON_COURSE_ID, newLesson.courseId)
        cv.put(KEY_LESSON_DATE, newLesson.date)
        cv.put(KEY_LESSON_DURATION, newLesson.duration)
        cv.put(KEY_LESSON_WEIGHT, newLesson.weight)
        if (db.update(TABLE_LESSONS, cv, KEY_LESSON_ID + " = " + oldLesson.id, null) == -1) status =
            false
        return status
    }

    //Обновить существующий урок
    fun updateLesson(oldLessonId: Int, newLesson: Lesson): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LESSON_NAME, newLesson.name)
        cv.put(KEY_LESSON_COURSE_ID, newLesson.courseId)
        cv.put(KEY_LESSON_DATE, newLesson.date)
        cv.put(KEY_LESSON_DURATION, newLesson.duration)
        cv.put(KEY_LESSON_WEIGHT, newLesson.weight)
        if (db.update(TABLE_LESSONS, cv, "$KEY_LESSON_ID = $oldLessonId", null) == -1) status =
            false
        return status
    }

    fun updateLessonSmart(lessonId: Int, lesson: Lesson): Boolean {
        var status = true
        val db = this.writableDatabase
        val parentCourse = getCourse(lesson.courseId.toLong())
        val mainLesson = getLesson(lessonId.toLong())
        if (parentCourse.startDate == mainLesson.date) {
            val cursor = db.rawQuery(
                "select * from " + TABLE_LESSONS + " where " + KEY_COURSE_ID + " = " + lesson.courseId + " and " + KEY_LESSON_DATE + " > " + mainLesson.date,
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                parentCourse.startDate = date
            } else {
                parentCourse.startDate = lesson.date
            }
            updateCourse(lesson.courseId, parentCourse)
            cursor.close()
        }
        status = updateLesson(lessonId, lesson)
        return status
    }

    //Удалить урок
    fun deleteLesson(lesson: Lesson): Boolean {
        var status = true
        val db = this.writableDatabase
        val parentCourse = getCourse(lesson.courseId.toLong())
        parentCourse.lessons = parentCourse.lessons - 1
        updateCourse(parentCourse.id, parentCourse)
        if (db.delete(TABLE_LESSONS, KEY_LESSON_ID + " = " + lesson.id, null) == -1) status = false
        if (deleteLessonOptions(getLessonOptions(lesson.id).id)) status = false
        return status
    }

    //Удалить урок
    fun deleteLesson(lessonId: Int): Boolean {
        var status = true
        val db = this.writableDatabase
        val lesson = getLesson(lessonId.toLong())
        val parentCourse = getCourse(lesson.courseId.toLong())
        parentCourse.lessons = parentCourse.lessons - 1
        updateCourse(parentCourse.id, parentCourse)
        if (db.delete(TABLE_LESSONS, "$KEY_LESSON_ID = $lessonId", null) == -1) status = false
        if (deleteLessonOptions(getLessonOptions(lessonId).id)) status = false
        return status
    }

    //Получить все уроки из БД
    val allLessons: ArrayList<Lesson>
        get() {
            val arrayList = ArrayList<Lesson>()
            val db = this.readableDatabase
            val cursor = db.rawQuery("select * from $TABLE_LESSONS", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val lesson = Lesson()
                    lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                    lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                    lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                    lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                    lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                    lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
                    arrayList.add(lesson)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить все уроки из БД с одинаковым родительским курсом
    fun getAllLessons(course: Course): ArrayList<Lesson> {
        val arrayList = ArrayList<Lesson>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from " + TABLE_LESSONS + " where " + KEY_LESSON_COURSE_ID + " = " + course.id,
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val lesson = Lesson()
                lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
                arrayList.add(lesson)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return arrayList
    }

    //Получить все уроки из БД с одинаковым родительским курсом
    fun getAllLessons(courseId: Int): ArrayList<Lesson> {
        val arrayList = ArrayList<Lesson>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_LESSONS where $KEY_LESSON_COURSE_ID = $courseId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val lesson = Lesson()
                lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
                arrayList.add(lesson)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return arrayList
    }

    //Получить все курсы за сегодняшний дней, отсортированные по времени
    val lessonsTodaySortByTime: ArrayList<Lesson>
        get() {
            val arrayList = ArrayList<Lesson>()
            val db = this.readableDatabase
            val today = Calendar.getInstance()
            today[Calendar.HOUR_OF_DAY] = 0
            val dayTime = today.timeInMillis / 1000
            val tomorrow = dayTime + 86400
            val cursor = db.rawQuery(
                "select * from $TABLE_LESSONS where $KEY_LESSON_DATE between $dayTime and $tomorrow order by $KEY_LESSON_DATE",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val lesson = Lesson()
                    lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                    lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                    lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                    lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                    lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                    lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
                    arrayList.add(lesson)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить все уроки за определенный день, отсортированные по времени
    fun getLessonsFromDaySortByTime(datetime: Long): ArrayList<Lesson> {
        val arrayList = ArrayList<Lesson>()
        val db = this.readableDatabase
        val today = Calendar.getInstance()
        today.timeInMillis = datetime
        today[Calendar.HOUR_OF_DAY] = 0
        val dayTime = today.timeInMillis / 1000
        val tomorrowTime = dayTime + 86400
        val cursor = db.rawQuery(
            "select * from $TABLE_LESSONS where $KEY_LESSON_DATE between $dayTime and $tomorrowTime order by $KEY_LESSON_DATE",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val lesson = Lesson()
                lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
                arrayList.add(lesson)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return arrayList
    }

    //Получить все уроки за сегодняшний день, отсортированные по весу
    val lessonTodaySortByWeight: Lesson
        get() {
            val lesson = Lesson()
            val db = this.readableDatabase
            val today = Calendar.getInstance()
            today[Calendar.HOUR_OF_DAY] = 0
            val dayTime = today.timeInMillis / 1000
            val cursor = db.rawQuery(
                "select * from $TABLE_LESSONS where $KEY_LESSON_DATE>=$dayTime order by $KEY_LESSON_WEIGHT",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
            }
            cursor.close()
            return lesson
        }

    //Получить ближайший к текущему времени урок
    val lessonFromNowSortByTime: Lesson
        get() {
            val lesson = Lesson()
            val db = this.readableDatabase
            val today = Calendar.getInstance()
            val dayTime = today.timeInMillis / 1000
            val cursor = db.rawQuery(
                "select * from $TABLE_LESSONS where $KEY_LESSON_DATE>=$dayTime order by $KEY_LESSON_DATE",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
                lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
                lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
                lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
                lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
                lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
            }
            cursor.close()
            return lesson
        }

    //Получить урок по ID
    fun getLesson(lessonId: Long): Lesson {
        val lesson = Lesson()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_LESSONS where $KEY_LESSON_ID = $lessonId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            lesson.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_ID))
            lesson.name = cursor.getString(cursor.getColumnIndex(KEY_LESSON_NAME))
            lesson.courseId = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_COURSE_ID))
            lesson.date = cursor.getLong(cursor.getColumnIndex(KEY_LESSON_DATE))
            lesson.duration = cursor.getFloat(cursor.getColumnIndex(KEY_LESSON_DURATION))
            lesson.weight = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_WEIGHT))
        }
        cursor.close()
        return lesson
    }

    //Получить длительность уроков за промежуток времени
    fun getLessonDurationInTime(startDate: Long, endDate: Long): Float {
        var startDate = startDate
        var endDate = endDate
        var sum = 0f
        val db = this.readableDatabase
        startDate /= 1000
        endDate /= 1000
        val cursor = db.rawQuery(
            "select SUM($KEY_LESSON_DURATION) from $TABLE_LESSONS where $KEY_LESSON_DATE between $startDate and $endDate",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            sum = cursor.getFloat(0)
        }
        cursor.close()
        return sum
    }

    //Получить длительность уроков с одинаковым родительским курсом с его начала по текущее время
    fun getLessonDurationByCourse(courseId: Int): Float {
        var sum = 0f
        val db = this.readableDatabase
        val course = getCourse(courseId.toLong())
        val calendar = Calendar.getInstance()
        val startDate = course.startDate
        val endDate = calendar.timeInMillis / 1000
        val cursor = db.rawQuery(
            "select SUM($KEY_LESSON_DURATION) from $TABLE_LESSONS where $KEY_LESSON_DATE <= $endDate and $KEY_LESSON_COURSE_ID = $courseId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            sum = cursor.getFloat(0)
        }
        cursor.close()
        return sum
    }

    //LESSON OPTIONS
    //Добавить опции урока
    fun insertLessonOptions(lessonOptions: LessonOptions): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LESSON_OPTIONS_LESSON_ID, lessonOptions.lessonId)
        cv.put(KEY_LESSON_OPTIONS_CALENDAR_EVENT_ID, lessonOptions.calendarEventId)
        cv.put(KEY_LESSON_OPTIONS_IS_REPEATABLE, lessonOptions.isRepeatable)
        cv.put(KEY_LESSON_OPTIONS_REPEAT_MODE, lessonOptions.repeatMode)
        cv.put(KEY_LESSON_OPTIONS_DESCRIPTION, lessonOptions.description)
        if (db.insert(TABLE_LESSON_OPTIONS, null, cv) == -1L) status = false
        return status
    }

    fun getLessonOptions(lessonId: Int): LessonOptions {
        val lessonOptions = LessonOptions()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_LESSON_OPTIONS where $KEY_LESSON_OPTIONS_LESSON_ID = $lessonId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            lessonOptions.id = cursor.getInt(cursor.getColumnIndex(KEY_LESSON_OPTIONS_ID))
            lessonOptions.lessonId = lessonId
            lessonOptions.calendarEventId =
                cursor.getInt(cursor.getColumnIndex(KEY_LESSON_OPTIONS_CALENDAR_EVENT_ID))
            lessonOptions.isRepeatable =
                cursor.getInt(cursor.getColumnIndex(KEY_LESSON_OPTIONS_IS_REPEATABLE))
            lessonOptions.repeatMode =
                cursor.getInt(cursor.getColumnIndex(KEY_LESSON_OPTIONS_REPEAT_MODE))
            lessonOptions.description =
                cursor.getString(cursor.getColumnIndex(KEY_LESSON_OPTIONS_DESCRIPTION))
            cursor.moveToNext()
        }
        cursor.close()
        return lessonOptions
    }

    fun deleteLessonOptions(id: Int): Boolean {
        var status = true
        val db = this.writableDatabase
        if (db.delete(
                TABLE_LESSON_OPTIONS,
                "$KEY_LESSON_OPTIONS_ID = $id",
                null
            ) == -1
        ) status = false
        return status
    }

    fun updateLessonOptions(id: Int, lessonOptions: LessonOptions): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_LESSON_OPTIONS_LESSON_ID, lessonOptions.lessonId)
        cv.put(KEY_LESSON_OPTIONS_CALENDAR_EVENT_ID, lessonOptions.calendarEventId)
        cv.put(KEY_LESSON_OPTIONS_IS_REPEATABLE, lessonOptions.isRepeatable)
        cv.put(KEY_LESSON_OPTIONS_REPEAT_MODE, lessonOptions.repeatMode)
        cv.put(KEY_LESSON_OPTIONS_DESCRIPTION, lessonOptions.description)
        if (db.update(
                TABLE_LESSON_OPTIONS,
                cv,
                "$KEY_LESSON_OPTIONS_ID = $id",
                null
            ) == -1
        ) status = false
        return status
    }

    //TEST
    //Добавить тест
    fun insertTest(test: Test): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_TEST_NAME, test.name)
        cv.put(KEY_TEST_COURSE_ID, test.courseId)
        cv.put(KEY_TEST_DATE, test.date)
        cv.put(KEY_TEST_WEIGHT, test.weight)
        if (db.insert(TABLE_TESTS, null, cv) == -1L) status = false
        return status
    }

    //Обновить существующий тест
    fun updateTest(oldTest: Test, test: Test): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_TEST_NAME, test.name)
        cv.put(KEY_TEST_COURSE_ID, test.courseId)
        cv.put(KEY_TEST_DATE, test.date)
        cv.put(KEY_TEST_WEIGHT, test.weight)
        if (db.update(TABLE_TESTS, cv, KEY_TEST_ID + " = " + oldTest.id, null) == -1) status = false
        return status
    }

    //Обновить существующий тест
    fun updateTest(testId: Int, test: Test): Boolean {
        var status = true
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_TEST_NAME, test.name)
        cv.put(KEY_TEST_COURSE_ID, test.courseId)
        cv.put(KEY_TEST_DATE, test.date)
        cv.put(KEY_TEST_WEIGHT, test.weight)
        if (db.update(TABLE_TESTS, cv, "$KEY_TEST_ID = $testId", null) == -1) status = false
        return status
    }

    //Удалить тест
    fun deleteTest(test: Test): Boolean {
        var status = true
        val db = this.writableDatabase
        if (db.delete(TABLE_TESTS, KEY_TEST_ID + " = " + test.id, null) == -1) status = false
        return status
    }

    //Удалить тест
    fun deleteTest(testId: Int): Boolean {
        var status = true
        val db = this.writableDatabase
        if (db.delete(TABLE_TESTS, "$KEY_TEST_ID = $testId", null) == -1) status = false
        return status
    }

    //Получить все тесты
    val allTests: ArrayList<Test>
        get() {
            val arrayList = ArrayList<Test>()
            val db = this.readableDatabase
            val cursor = db.rawQuery("select * from $TABLE_TESTS", null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val test = Test()
                    test.id = cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID))
                    test.name = cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME))
                    test.courseId = cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID))
                    test.date = cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE))
                    test.weight = cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT))
                    arrayList.add(test)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            return arrayList
        }

    //Получить все тесты с одинаковым родительским курсом
    fun getAllTests(courseId: Int): ArrayList<Test> {
        val arrayList = ArrayList<Test>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_TESTS where $KEY_TEST_COURSE_ID = $courseId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val test = Test()
                test.id = cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID))
                test.name = cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME))
                test.courseId = cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID))
                test.date = cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE))
                test.weight = cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT))
                arrayList.add(test)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return arrayList
    }

    //Получить все тесты за определенный день, отсортированные по времени
    fun getTestsFromDaySortByTime(datetime: Long): ArrayList<Test> {
        val arrayList = ArrayList<Test>()
        val db = this.readableDatabase
        val today = Calendar.getInstance()
        today.timeInMillis = datetime
        today[Calendar.HOUR_OF_DAY] = 0
        val dayTime = today.timeInMillis / 1000
        val tomorrowTime = dayTime + 86400
        val cursor = db.rawQuery(
            "select * from $TABLE_TESTS where $KEY_TEST_DATE between $dayTime and $tomorrowTime order by $KEY_TEST_DATE",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val test = Test()
                test.id = cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID))
                test.name = cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME))
                test.courseId = cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID))
                test.date = cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE))
                test.weight = cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT))
                arrayList.add(test)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return arrayList
    }

    //Получить ближаший к текущему времени тест
    val testFromNowSortByTime: Test
        get() {
            val test = Test()
            val db = this.readableDatabase
            val today = Calendar.getInstance()
            val dayTime = today.timeInMillis / 1000
            val cursor = db.rawQuery(
                "select * from $TABLE_TESTS where $KEY_TEST_DATE>=$dayTime order by $KEY_TEST_DATE",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                test.id = cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID))
                test.name = cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME))
                test.courseId = cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID))
                test.date = cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE))
                test.weight = cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT))
            }
            cursor.close()
            return test
        }

    //Получить урок по ID
    fun getTest(testId: Int): Test {
        val test = Test()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "select * from $TABLE_TESTS where $KEY_TEST_ID = $testId",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            test.id = cursor.getInt(cursor.getColumnIndex(KEY_TEST_ID))
            test.name = cursor.getString(cursor.getColumnIndex(KEY_TEST_NAME))
            test.courseId = cursor.getInt(cursor.getColumnIndex(KEY_TEST_COURSE_ID))
            test.date = cursor.getLong(cursor.getColumnIndex(KEY_TEST_DATE))
            test.weight = cursor.getInt(cursor.getColumnIndex(KEY_TEST_WEIGHT))
        }
        cursor.close()
        return test
    }

    //TOTAL
    val eventFromNowSortByTime: Any
        get() {
            val lesson = lessonFromNowSortByTime
            val test = testFromNowSortByTime
            return if (test.name == null) lesson else if (lesson.name == null) test else if (test.date <= lesson.date) test else lesson
        }//calendar.setTimeInMillis(test.getDate()*1000);

    //str = test.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
//calendar.setTimeInMillis(lesson.getDate()*1000);
    //str = lesson.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
    //Получить название ближайшего к текущему времени события (урока или теста)
    val eventFromNowSortByTimeStr: String?
        get() {
            var str: String? = null
            val calendar = Calendar.getInstance()
            val lesson = lessonFromNowSortByTime
            val test = testFromNowSortByTime
            if (test.name == null || test.date >= lesson.date) {
                //calendar.setTimeInMillis(lesson.getDate()*1000);
                //str = lesson.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
                val names = lesson.name!!.split(", ").toTypedArray()
                str = names[0] + " lesson at " + names[2] + " " + names[1]
            } else if (lesson.name == null || test.date <= lesson.date) {
                //calendar.setTimeInMillis(test.getDate()*1000);
                //str = test.getName() + " at "+ calendar.get(Calendar.AM_PM) + " " + calendar.get(Calendar.DAY_OF_WEEK);
                val names = test.name!!.split(", ").toTypedArray()
                str = names[0] + " at " + names[2] + " " + names[1]
            }
            return str
        }

    //Получить названия всех событий (уроков или тестов) за определенный день, отсортированных по времени
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getEventFromDaySortByTime(datetime: Long): ArrayList<Event> {
        val events = ArrayList<Event>()
        val lessons = getLessonsFromDaySortByTime(datetime)
        val tests = getTestsFromDaySortByTime(datetime)
        lessons.forEach(Consumer { lesson: Lesson ->
            events.add(
                Event(
                    lesson.id,
                    lesson.id,
                    lesson.name,
                    lesson.courseId,
                    lesson.date,
                    lesson.duration,
                    Event.EventType.Lesson
                )
            )
        })
        tests.forEach(Consumer { test: Test ->
            events.add(
                Event(
                    test.id,
                    test.id,
                    test.name,
                    test.courseId,
                    test.date,
                    0F,
                    Event.EventType.Test
                )
            )
        })
        events.sortWith { o1: Event, o2: Event -> o1.date.compareTo(o2.date) }
        return events
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getAllEvents(courseId: Int): ArrayList<Event> {
        val events = ArrayList<Event>()
        val lessons = getAllLessons(courseId)
        val tests = getAllTests(courseId)
        lessons.forEach(Consumer { lesson: Lesson ->
            events.add(
                Event(
                    lesson.id,
                    lesson.id,
                    lesson.name,
                    lesson.courseId,
                    lesson.date,
                    lesson.duration,
                    Event.EventType.Lesson
                )
            )
        })
        tests.forEach(Consumer { test: Test ->
            events.add(
                Event(
                    test.id,
                    test.id,
                    test.name,
                    test.courseId,
                    test.date,
                    0F,
                    Event.EventType.Test
                )
            )
        })
        return events
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RSSTUDIO.db"
        const val TABLE_COURSES = "Courses"
        const val KEY_COURSE_ID = "_id"
        const val KEY_COURSE_NAME = "name"
        const val KEY_COURSE_START_DATE = "startDate"
        const val KEY_COURSE_END_DATE = "endDate"
        const val KEY_COURSE_FINISHED = "finished"
        const val KEY_COURSE_LESSONS = "lessons"
        const val KEY_COURSE_COMPLETED_LESSONS = "lessonsCompleted"
        const val TABLE_LESSONS = "Lessons"
        const val KEY_LESSON_ID = "_id"
        const val KEY_LESSON_NAME = "name"
        const val KEY_LESSON_COURSE_ID = "courseId"
        const val KEY_LESSON_DATE = "date"
        const val KEY_LESSON_DURATION = "duration"
        const val KEY_LESSON_WEIGHT = "weight"
        const val TABLE_LESSON_OPTIONS = "LessonOptions"
        const val KEY_LESSON_OPTIONS_ID = "_id"
        const val KEY_LESSON_OPTIONS_LESSON_ID = "lessonId"
        const val KEY_LESSON_OPTIONS_CALENDAR_EVENT_ID = "calendarEventId"
        const val KEY_LESSON_OPTIONS_IS_REPEATABLE = "isRepeatable"
        const val KEY_LESSON_OPTIONS_REPEAT_MODE = "repeatMode"
        const val KEY_LESSON_OPTIONS_DESCRIPTION = "description"
        const val TABLE_TESTS = "Tests"
        const val KEY_TEST_ID = "_id"
        const val KEY_TEST_NAME = "name"
        const val KEY_TEST_COURSE_ID = "courseId"
        const val KEY_TEST_DATE = "date"
        const val KEY_TEST_WEIGHT = "weight"
    }
}