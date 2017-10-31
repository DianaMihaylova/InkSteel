package com.ink_steel.inksteel.model;

import java.util.ArrayList;

public class User {

    private String email;
    private String name;
    private String age;
    private String city;
    private String profileImage;
    private ArrayList<String> gallery;
    private ArrayList<String> friends;
    private ArrayList<String> likes;
    private ArrayList<String> likedBy;

    public User() {
    }

    public User(String email, String name, String age, String city, String profileImage) {
        this(email, name, age, city, profileImage,
                new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
    }

    public User(String email, String name, String age, String city, String profileImage,
                ArrayList<String> gallery, ArrayList<String> friends,
                ArrayList<String> likes, ArrayList<String> likedBy) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.city = city;
        this.profileImage = profileImage;
        this.gallery = gallery;
        this.friends = friends;
        this.likes = likes;
        this.likedBy = likedBy;
    }

    public void updateUserInfo(String name, String age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public ArrayList<String> getGallery() {
        return gallery;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }
}
