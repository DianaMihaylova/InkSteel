package com.ink_steel.inksteel.model;

import android.net.Uri;

import java.util.Date;

public class Post {

    private String user;
    private Date date;
    private Uri profileUri;
    private Uri imageUrl;

    public Post(String user, Date date, Uri profileUri, Uri imageUrl) {
        this.user = user;
        this.date = date;
        this.imageUrl = imageUrl;
        this.profileUri = profileUri;
    }

    public String getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public Uri getProfileUri() {
        return profileUri;
    }
}
