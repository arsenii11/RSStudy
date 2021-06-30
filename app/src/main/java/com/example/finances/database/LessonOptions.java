package com.example.finances.database;

public class LessonOptions {
    private int id;
    private int lessonId;
    private int calendarEventId;
    private int isRepeatable;
    private int repeatMode;

    public LessonOptions() {}

    public LessonOptions(int id, int lessonId, int calendarEventId, int isRepeatable, int repeatMode){
        this.id = id;
        this.lessonId = lessonId;
        this.calendarEventId = calendarEventId;
        this.isRepeatable = isRepeatable;
        this.repeatMode = repeatMode;
    }

    public int getId() {
        return id;
    }

    public int getLessonId() {
        return lessonId;
    }

    public int getCalendarEventId() {
        return calendarEventId;
    }

    public int getIsRepeatable() {
        return isRepeatable;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public void setCalendarEventId(int calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public void setIsRepeatable(int isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }
}