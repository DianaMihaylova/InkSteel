package com.ink_steel.inksteel.model;

import com.ink_steel.inksteel.R;

public class Reaction {

    private boolean initial;
    private String userEmail;
    private String reactionType;
    private long time;

    public Reaction() {
    }

    public Reaction(String user, String reactionType, boolean isInitial, long time) {
        this.userEmail = user;
        this.reactionType = reactionType;
        this.initial = isInitial;
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getReactionType() {
        return reactionType;
    }

    public long getTime() {
        return time;
    }

    public boolean getInitial() {
        return initial;
    }

    public int getReactionIcon() {
        switch (reactionType) {
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

    public String getMessage() {
        return initial ? "reacted" : "updated reaction";
    }
}
