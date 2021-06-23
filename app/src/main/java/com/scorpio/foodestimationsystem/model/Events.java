package com.scorpio.foodestimationsystem.model;

import java.util.ArrayList;

public class Events {
    String id, user_id, name;
    int participants;
    long date;
    ArrayList<String> dishes;

    public Events() {
    }

    public Events(String id, String user_id, String name, int participants, long date, ArrayList<String> dishes) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.participants = participants;
        this.date = date;
        this.dishes = dishes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<String> dishes) {
        this.dishes = dishes;
    }
}
