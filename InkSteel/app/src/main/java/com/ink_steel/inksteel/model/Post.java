package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Collections;

public class Post {

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
                ArrayList<Integer> reactions) {
        this.postId = postId;
        this.userEmail = userEmail;
        this.urlProfileImage = urlProfileImage;
        this.urlImage = urlImage;
        this.createdAt = createdAt;
        this.urlThumbnailImage = urlThumbnailImage;
        this.description = description;
        if (reactions.size() == 0) {
            Collections.addAll(reactions, 0, 0, 0, 0);
        }
        this.reactions = reactions;
    }

    public String getPostId() {
        return postId;
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
    public int getReactionCount(ReactionType type) {
        switch (type) {
            case LIKE:
                return reactions.get(0);
            case BLUSH:
                return reactions.get(1);
            case DEVIL:
                return reactions.get(2);
            case DAZED:
                return reactions.get(3);
        }
        return 0;
    }

    @Exclude
    public void addReaction(ReactionType type) {
        int reaction;
        switch (type) {
            case LIKE:
                reaction = reactions.get(0) + 1;
                reactions.set(0, reaction);
                break;
            case BLUSH:
                reaction = reactions.get(1) + 1;
                reactions.set(1, reaction);
                break;
            case DEVIL:
                reaction = reactions.get(2) + 1;
                reactions.set(2, reaction);
                break;
            case DAZED:
                reaction = reactions.get(3) + 1;
                reactions.set(3, reaction);
                break;
        }
    }

    @Exclude
    public void removeReaction(ReactionType type) {
        int reaction;
        switch (type) {
            case LIKE:
                reaction = reactions.get(0) - 1;
                reactions.set(0, reaction);
                break;
            case BLUSH:
                reaction = reactions.get(1) - 1;
                reactions.set(1, reaction);
                break;
            case DEVIL:
                reaction = reactions.get(2) - 1;
                reactions.set(2, reaction);
                break;
            case DAZED:
                reaction = reactions.get(3) - 1;
                reactions.set(3, reaction);
                break;
        }
    }

    public enum ReactionType {
        LIKE, BLUSH, DEVIL, DAZED
    }

}
