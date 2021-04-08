package com.scorpio.foodestimationsystem.model;

import java.util.ArrayList;

public class Events {
    String name;
    int participants;
    long date;
    ArrayList<String> dishes;

    public Events() {
    }

    public Events(String name, int participants, long date, ArrayList<String> dishes) {
        this.name = name;
        this.participants = participants;
        this.date = date;
        this.dishes = dishes;
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
