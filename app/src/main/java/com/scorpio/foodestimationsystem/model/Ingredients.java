package com.scorpio.foodestimationsystem.model;

public class Ingredients {
    String id, name, unit;
    int quantity, price;

    public Ingredients() {
    }

    public Ingredients(String id, String name, String unit, int quantity, int price) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
