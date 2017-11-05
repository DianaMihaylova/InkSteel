package com.ink_steel.inksteel.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Collections;

public class Post implements Comparable<Post> {

    private String postId;
    private String userEmail;
    private long createdAt;
    private String urlProfileImage;
    private String urlImage;
    private String urlThumbnailImage;
    private String description;
    private ArrayList<Integer> reactions;

    public Post() {
    }

    public Post(String postId, String userEmail, long createdAt, String urlProfileImage,
                String urlImage, String urlThumbnailImage, String description,
                int like, int blush, int devil, int dazed) {
        this(postId, userEmail, createdAt, urlProfileImage,
                urlImage, urlThumbnailImage, description);
        reactions.clear();
        reactions.add(like);
        reactions.add(blush);
        reactions.add(devil);
        reactions.add(dazed);
    }

    public Post(String postId, String userEmail, long createdAt, String urlProfileImage,
                String urlImage, String urlThumbnailImage, String description) {
        this.postId = postId;
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.urlProfileImage = urlProfileImage;
        this.urlImage = urlImage;
        this.urlThumbnailImage = urlThumbnailImage;
        this.description = description;
        reactions = new ArrayList<>();
        Collections.addAll(reactions, 0, 0, 0, 0);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getUrlProfileImage() {
        return urlProfileImage;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getPostId() {
        return postId;
    }

    public String getUrlThumbnailImage() {
        return urlThumbnailImage;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Integer> getReactions() {
        return reactions;
    }

    @Exclude
    public int getReactionCount(String type) {
        if (type == null)
            return 0;
        switch (type) {
            case "like":
                return reactions.get(0);
            case "blush":
                return reactions.get(1);
            case "devil":
                return reactions.get(2);
            case "dazed":
                return reactions.get(3);
        }
        return 0;
    }

    @Override
    public int compareTo(@NonNull Post post) {
        if (postId.equals(post.postId))
            return 0;
        return (int) (post.createdAt - createdAt);
    }
}