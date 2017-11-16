package com.ink_steel.inksteel.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Studio {

    @SerializedName("formatted_address")
    @Expose
    private String address;

    private LatLng location;

    @SerializedName("name")
    @Expose
    private String name;

    private boolean isOpenNow;

    private String photoReference;

    private String photoUrl;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("rating")
    @Expose
    private float rating;

    public String getAddress() {
        return address;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public void setOpenNow(boolean openNow) {
        isOpenNow = openNow;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getPlaceId() {
        return placeId;
    }

    public float getRating() {
        return rating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        if (location != null) {
            return placeId + " " + isOpenNow + " " + photoReference + " " + location.toString();
        }
        return placeId + " " + isOpenNow + " " + photoReference + " " + null;
    }
}
