package com.example.finances.database;

public class Lesson {

    private String name;
    private int courseId;
    private int date;
    private int duration;
    private int weight;

    public Lesson(){}

    public Lesson(String name, int courseId, int date, int duration, int weight){
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.weight = weight;
    }

    public String getName(){
        return name;
    }

    public int getCourseId(){
        return courseId;
    }

    public int getDate(){
        return date;
    }

    public int getDuration(){
        return duration;
    }

    public int getWeight(){
        return weight;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCourseId(int courseId){
        this.courseId = courseId;
    }

    public void setDate(int date){
        this.date = date;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
}
