package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;
import com.ink_steel.inksteel.R;
import com.ink_steel.inksteel.model.Post.ReactionType;

public class Reaction {

    private boolean initial;
    private String userEmail;
    private ReactionType type;
    private long time;

    public Reaction() {
    }

    public Reaction(String userEmail, ReactionType type, boolean isInitial, long time) {
        this.userEmail = userEmail;
        this.type = type;
        this.initial = isInitial;
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ReactionType getType() {
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
            case LIKE:
                return R.drawable.like;
            case BLUSH:
                return R.drawable.blush;
            case DEVIL:
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