package com.example.finances.ui.Account;


public class Course {

    private String name; // название
    private String capital;  // столица
    private int flagResource; // ресурс флага

    public Course(String name, String capital, int flag){

        this.name=name;
        this.capital=capital;
        this.flagResource=flag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return this.capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public int getFlagResource() {
        return this.flagResource;
    }

    public void setFlagResource(int flagResource) {
        this.flagResource = flagResource;
    }
}
