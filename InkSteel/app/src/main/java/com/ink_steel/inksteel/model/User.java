package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String email;
    private String name;
    private String age;
    private String city;
    private String profileImage;
    private ArrayList<String> gallery;
    private ArrayList<String> friends;
    private ArrayList<String> liked;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String name, String age, String city, String profileImage,
                ArrayList<String> gallery, ArrayList<String> friends, ArrayList<String> liked) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.city = city;
        this.profileImage = profileImage;
        this.gallery = gallery;
        this.friends = friends;
        this.liked = liked;
    }

    @Exclude
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
        if (profileImage != null && !profileImage.isEmpty())
            this.profileImage = profileImage;
    }

    public ArrayList<String> getGallery() {
        if (gallery == null)
            gallery = new ArrayList<>();
        return gallery;
    }

    public ArrayList<String> getFriends() {
        if (friends == null)
            friends = new ArrayList<>();
        return friends;
    }

    public ArrayList<String> getLiked() {
        if (liked == null)
            liked = new ArrayList<>();
        return liked;
    }

    @Exclude
    public Map<String, Object> getUserInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", name);
        info.put("city", city);
        info.put("age", age);
        info.put("profileImage", profileImage);
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && email.equals(((User) obj).email);
    }

}
