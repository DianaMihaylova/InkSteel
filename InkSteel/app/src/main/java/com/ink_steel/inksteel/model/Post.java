package com.ink_steel.inksteel.model;

import android.net.Uri;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class Post {

    private String user;
    private long date;
    private String profileUri;
    private String imageUrl;
    private String postId;
    private String thumbnailUrl;

    public Post(String postId, String user, long date, String profilePictureUri,
                String postImageUri, String thumbnailUrl) {
        this.postId = postId;
        this.user = user;
        this.date = date;
        this.profileUri = profilePictureUri;
        this.imageUrl = postImageUri;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPostId() {
        return postId;
    }

    public String getDate() {

        return new SimpleDateFormat("h:mm, dd MMMM", Locale.US).format(date);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Post && postId.equals(((Post) obj).postId);

    }
}
