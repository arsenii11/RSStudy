package com.example.finances.database;

public class Test {

    private int id;
    private String name;
    private int courseId;
    private long date;
    private int weight;

    public Test(){}

    public Test(int id, String name, int courseId, long date, int weight){
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.date = date;
        this.weight = weight;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getCourseId() { return courseId; }

    public long getDate() { return date; }

    public int getWeight() { return weight; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setCourseId(int courseId) { this.courseId = courseId; }

    public void setDate(long date) { this.date = date; }

    public void setWeight(int weight) { this.weight = weight; }

}
