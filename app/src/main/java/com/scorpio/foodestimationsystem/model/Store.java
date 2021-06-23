package com.scorpio.foodestimationsystem.model;

public class Store {
    String name;
    int quantityAvailable, quantityRequired;

    public Store() {
    }

    public Store(String name, int quantityAvailable, int quantityRequired) {
        this.name = name;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequired = quantityRequired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }
}
