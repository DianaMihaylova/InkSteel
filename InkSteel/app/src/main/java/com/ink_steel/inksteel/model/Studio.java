package com.ink_steel.inksteel.model;

public class Studio {

    private String name;
    private float rating;
    private String address;
    private String photoReference;
    private String placeId;

    public Studio(String name, float rating, String address, String id, String photo) {
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.placeId = id;
        this.photoReference = photo;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Studio && placeId.equals(((Studio) obj).placeId);
    }
}
