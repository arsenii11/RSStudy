package com.example.finances.database;

public class Event {

    private int id;
    private int eventId;
    private String name;
    private int courseId;
    private long date;
    private float duration;
    private EventType eventType;

    public enum EventType{
        Lesson,
        Test
    }

    public Event() {}

    public Event(int id, int eventId,String name, int courseId, long date, float duration, EventType eventType) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.eventType = eventType;
    }

    public int getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public int getCourseId() {
        return courseId;
    }

    public long getDate() {
        return date;
    }

    public float getDuration() {
        return duration;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
