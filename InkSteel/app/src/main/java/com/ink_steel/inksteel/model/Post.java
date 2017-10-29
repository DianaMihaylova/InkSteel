package com.ink_steel.inksteel.model;

public class Post {

    private String postId;
    private String userEmail;
    private long createdAt;
    private String urlProfileImage;
    private String urlImage;
    private String urlThumbnailImage;
    private String description;
    private int reactionLike;
    private int reactionBlush;
    private int reactionDevil;
    private int reactionDazed;

    public Post() {
    }

    public Post(String postId, String userEmail, long createdAt, String urlProfileImage,
                String urlImage, String urlThumbnailImage, String description,
                int reactionLike, int reactionBlush, int reactionDevil, int reactionDazed) {
        this(postId, userEmail, createdAt, urlProfileImage, urlImage, urlThumbnailImage, description);
        this.reactionLike = reactionLike;
        this.reactionBlush = reactionBlush;
        this.reactionDevil = reactionDevil;
        this.reactionDazed = reactionDazed;
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

    public int getReactionLike() {
        return reactionLike;
    }

    public int getReactionBlush() {
        return reactionBlush;
    }

    public int getReactionDevil() {
        return reactionDevil;
    }

    public int getReactionDazed() {
        return reactionDazed;
    }

    public int getReactionCount(String type) {
        switch (type) {
            case "like":
                return reactionLike;
            case "blush":
                return reactionBlush;
            case "devil":
                return reactionDevil;
            case "dazed":
                return reactionDazed;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Post && postId.equals(((Post) obj).postId);
    }
}
