package com.scorpio.foodestimationsystem.modal;

public class Dishes {
    String dishImage, dishName;

    public Dishes(String dishImage, String dishName) {
        this.dishImage = dishImage;
        this.dishName = dishName;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
}
