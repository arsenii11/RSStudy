package com.example.finances.database;

public class Event {

    public int id;
    public String name;
    public int courseId;
    public long date;
    public EventType eventType;

    public enum EventType{
        Lesson,
        Test
    }

    public Event() {}

    public Event(int id, String name, int courseId, long date, EventType eventType) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.eventType = eventType;
    }

    public int getId() {
        return id;
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

    public EventType getEventType() {
        return eventType;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
