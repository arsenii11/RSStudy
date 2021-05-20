package com.example.finances.database;

public class Course {

    private  int id;
    private String name;
    private long startDate;
    private long endDate;
    private int finished;
    private int lessons;
    private int lessonsCompleted;

    public Course() {}

    public Course(int id, String name, long startDate, long endDate, int finished, int lessons, int lessonsCompleted){
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.finished = finished;
        this.lessons = lessons;
        this.lessonsCompleted = lessonsCompleted;
    }

    public int getId() {return id;}

    public String getName(){
        return name;
    }

    public long getStartDate(){
        return startDate;
    }

    public long getEndDate(){
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

    public void setId(int id) { this.id = id; }

    public void setName(String name){
        this.name = name;
    }

    public void setStartDate(long startDate){
        this.startDate = startDate;
    }

    public void setEndDate(long endDate){
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
