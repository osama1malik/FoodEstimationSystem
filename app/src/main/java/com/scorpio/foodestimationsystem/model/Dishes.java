package com.scorpio.foodestimationsystem.model;

import java.util.ArrayList;

public class Dishes {
    String id, image, name;
    int price;
    ArrayList<String> ingredients;
    ArrayList<Integer> quantity;

    public Dishes() {
    }

    public Dishes(String id, String image, String name, int price, ArrayList<String> ingredients, ArrayList<Integer> quantity) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.quantity = quantity;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(ArrayList<Integer> quantity) {
        this.quantity = quantity;
    }
}
