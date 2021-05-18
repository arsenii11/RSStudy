package com.example.finances.database;

public class Course {

    private String name;
    private int startDate;
    private int endDate;
    private int finished;
    private int lessons;
    private int lessonsCompleted;

    public Course() {}

    public Course(String name, int startDate, int endDate, int finished, int lessons, int lessonsCompleted){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.finished = finished;
        this.lessons = lessons;
        this.lessonsCompleted = lessonsCompleted;
    }

    public String getName(){
        return name;
    }

    public  int getStartDate(){
        return startDate;
    }

    public int getEndDate(){
        return endDate;
    }

    public int getFinished(){
        return finished;
    }

    public int getLessons(){
        return lessons;
    }

    public int getLessonsCompleted(){
        return lessonsCompleted;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setStartDate(int startDate){
        this.startDate = startDate;
    }

    public void setEndDate(int endDate){
        this.endDate = endDate;
    }

    public void setFinished(int finished){
        this.finished = finished;
    }

    public void setLessons(int lessons){
        this.lessons = lessons;
    }

    public void setLessonsCompleted(int lessonsCompleted){
        this.lessonsCompleted = lessonsCompleted;
    }

}
