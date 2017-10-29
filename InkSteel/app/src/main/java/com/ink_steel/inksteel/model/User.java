package com.ink_steel.inksteel.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String userEmail;
    private String userName;
    private String userCity;
    private String profileImg;
    private ArrayList<String> likes;

    public User() {
    }

    public User(String userEmail, String userName, String userCity, String profileImg) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userCity = userCity;
        this.profileImg = profileImg;
        likes = new ArrayList<>();
        likes.add("like1");
        likes.add("like2");
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }
}
