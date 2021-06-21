package com.example.finances.database;

public class Lesson {

    private int id;
    private String name;
    private int courseId;
    private long date;
    private float duration;
    private int weight;
    private int calendarEventId;

    public Lesson(){}

    public Lesson(int id, String name, int courseId, long date, float duration, int weight, int calendarEventId){
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.weight = weight;
        this.calendarEventId = calendarEventId;
    }

    public int getId() { return id; }

    public String getName(){
        return name;
    }

    public int getCourseId(){
        return courseId;
    }

    public long getDate(){
        return date;
    }

    public float getDuration(){
        return duration;
    }

    public int getWeight(){
        return weight;
    }

    public int getCalendarEventId() {
        return calendarEventId;
    }

    public void setId(int id) { this.id = id; }

    public void setName(String name){
        this.name = name;
    }

    public void setCourseId(int courseId){
        this.courseId = courseId;
    }

    public void setDate(long date){
        this.date = date;
    }

    public void setDuration(float duration){
        this.duration = duration;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setCalendarEventId(int calendarEventId) {
        this.calendarEventId = calendarEventId;
    }
}
