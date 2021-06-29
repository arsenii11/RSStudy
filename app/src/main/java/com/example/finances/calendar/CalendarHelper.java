package com.example.finances.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import androidx.annotation.RequiresApi;
import androidx.loader.content.CursorLoader;

import com.example.finances.database.DBHelper;
import com.example.finances.database.LessonOptions;

import java.util.TimeZone;

public class CalendarHelper {

    public final static Uri calendars = Uri.parse("content://com.android.calendar/calendars"); //Адрес существующих календарей
    public final static Uri events = Uri.parse("content://com.android.calendar/events"); //Адрес событий в календарях

    int calendarId; //ID календаря
    ContentResolver contentResolver; //Создаем обработчика запросов к календарю
    DBHelper dbHelper; //Создаем обработчика запросов к БД

    //Конструктор класса
    public CalendarHelper(Context context){
        //Создаем намерение запроса в БД системного календаря
        CursorLoader cursorLoader = new CursorLoader(context,
                calendars,
                new String[]{"_id"},
                null,
                null,
                null);
        Cursor cursor = cursorLoader.loadInBackground(); //Получаем указатель на ответ БД

        //Проверяем не пустой ли ответ БД и ставим указатель в начало
        if (cursor.getCount() > 0 && cursor.moveToFirst())
        {
            //проходим по ответу БД и ищем ID последнего календаря
            while (!cursor.isAfterLast()) {
                calendarId = cursor.getInt(cursor.getColumnIndex("_id")); //Записываем данные из ответа БД в переменную
                cursor.moveToNext(); //Идем к следующей строке ответа
            }
            cursor.close(); //Закрываем запрос
        }

        contentResolver = context.getContentResolver(); //Устанавливаем обработчик запросов к календарю
        dbHelper = new DBHelper(context); //Устанавливаем обработчик запросов к БД
    }

    //Функция для добавления нового события в календарь
    public int addCalendarEvent(String name, long startDate, long endDate){
        ContentValues calendarEvent = new ContentValues(); //Создаем массив для отправляемых данных
        calendarEvent.put(CalendarContract.Events.CALENDAR_ID, calendarId); //ID календаря
        calendarEvent.put(CalendarContract.Events.TITLE, name); //Название события
        calendarEvent.put(CalendarContract.Events.DTSTART,startDate); //Дата начала события
        calendarEvent.put(CalendarContract.Events.DTEND, endDate); //Дата окончания
        calendarEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName()); //Временная зона (без неё не отправляется запрос)
        Uri uri = contentResolver.insert(events, calendarEvent); //Отправялем запрос
        int eventId = Integer.parseInt(uri.getLastPathSegment()); //Получаем ID установленного события
        return eventId;
    }

    //Функция удаления события из календаря
    public void deleteCalendarEvent(int eventId){
        if(eventId > -1) {
            Uri uri = ContentUris.withAppendedId(events, eventId);
            contentResolver.delete(uri, null, null);
        }
    }

    //Функция удаления всех событий в календаре по ID родительского курса
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteAllCalendarEvent(int courseId){
        dbHelper.getAllLessons(courseId).forEach(lesson -> {
            LessonOptions lessonOptions = dbHelper.getLessonOptions(lesson.getId());
            if (lessonOptions.getCalendarEventId() > -1) deleteCalendarEvent(lessonOptions.getCalendarEventId());
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void updateCalendarEvent(int eventId, String name, long startDate, long endDate){
        if(eventId > -1) {
            Uri uri = ContentUris.withAppendedId(events, eventId);
            ContentValues calendarEvent = new ContentValues(); //Создаем массив для отправляемых данных
            calendarEvent.put(CalendarContract.Events.CALENDAR_ID, calendarId); //ID календаря
            calendarEvent.put(CalendarContract.Events.TITLE, name); //Название события
            calendarEvent.put(CalendarContract.Events.DTSTART, startDate); //Дата начала события
            calendarEvent.put(CalendarContract.Events.DTEND, endDate); //Дата окончания
            calendarEvent.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName()); //Временная зона (без неё не отправляется запрос)
            contentResolver.update(events, calendarEvent, null);
        }
    }
}
