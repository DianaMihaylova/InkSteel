package com.ink_steel.inksteel.model;

import com.google.android.gms.location.places.Place;

public class Studio {

    private String placeId;
    private String name;
    private float rating;
    private String imageUrl;
    private Place googlePlace;
    private boolean isOpenNow;

    public Studio(String name, float rating, String id, String imageUrl, boolean isOpenNow) {
        this.name = name;
        this.rating = rating;
        this.placeId = id;
        this.imageUrl = imageUrl;
        this.isOpenNow = isOpenNow;
    }

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setGooglePlace(Place googlePlace) {
        this.googlePlace = googlePlace;
    }

    public Place getGooglePlace() {
        return googlePlace;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }
}