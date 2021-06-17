package com.example.finances.database;

public class Event {
    public String name;
    public long date;

    public Event() {}

    public Event(String name, long date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
