package com.ink_steel.inksteel.model;

public class User {

    private String userName;
    private String userCity;
    private int profileImg;

    public User(String userName, String userCity, int profileImg) {
        this.userName = userName;
        this.userCity = userCity;
        this.profileImg = profileImg;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserCity() {
        return userCity;
    }

    public int getProfileImg() {
        return profileImg;
    }
}
