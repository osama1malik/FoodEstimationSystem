package com.scorpio.foodestimationsystem.model;

import java.util.ArrayList;

public class Dishes {
    String image, name;
    int price;
    ArrayList<String> ingredients;

    public Dishes() {
    }

    public Dishes(String image, String name, int price, ArrayList<String> ingredients) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
