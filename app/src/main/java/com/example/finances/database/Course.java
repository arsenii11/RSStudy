package com.example.finances.database;

public class Course {

    private  int id;
    private String name;
    private int startDate;
    private int endDate;
    private int finished;
    private int lessons;
    private int lessonsCompleted;

    public Course() {}

    public Course(int id, String name, int startDate, int endDate, int finished, int lessons, int lessonsCompleted){
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

    public void setId(int id) { this.id = id; }

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
