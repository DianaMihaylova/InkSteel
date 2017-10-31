package com.ink_steel.inksteel.model;

public class Studio {

    private String name;
    private float rating;
    private int imageId;

    public Studio(String name, float rating, int imageId) {
        this.name = name;
        this.rating = rating;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public int getImageId() {
        return imageId;
    }
}
