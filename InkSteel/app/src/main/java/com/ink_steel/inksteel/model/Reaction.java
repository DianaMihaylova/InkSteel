package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;
import com.ink_steel.inksteel.R;

public class Reaction {

    private boolean initial;
    private String userEmail;
    private String type;
    private long time;

    public Reaction() {
    }

    public Reaction(String user, String type, boolean isInitial, long time) {
        this.userEmail = user;
        this.type = type;
        this.initial = isInitial;
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public boolean getInitial() {
        return initial;
    }

    @Exclude
    public int getReactionIcon() {
        switch (type) {
            case "like":
                return R.drawable.like;
            case "blush":
                return R.drawable.blush;
            case "devil":
                return R.drawable.devil;
            default:
                return R.drawable.dazed;
        }
    }

    @Exclude
    public String getMessage() {
        return initial ? "reacted" : "updated reaction";
    }
}