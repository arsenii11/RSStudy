package com.example.finances.events.newevent;

public class NewEvent {


    private String newEventName;

    public NewEvent(String newEventName){

        this.newEventName = newEventName;
    }
    public String getNewEventname(){

        return this.newEventName;
    }
    public void setNewEventname(){
        this.newEventName = newEventName;
    }

}
