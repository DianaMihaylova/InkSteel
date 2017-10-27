package com.ink_steel.inksteel.model;

import com.ink_steel.inksteel.R;

public class Reaction {

    boolean isInitial;
    private String user;
    private String reactionType;

    public Reaction(String user, String reactionType, boolean isInitial) {
        this.user = user;
        this.reactionType = reactionType;
        this.isInitial = isInitial;
    }

    public String getUser() {
        return user;
    }

    public String getReactionType() {
        return reactionType;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public int getReactionImage() {
        switch (reactionType) {
            case "like":
                return R.drawable.like;
            case "blush":
                return R.drawable.blush;
            case "devil":
                return R.drawable.devil;
            case "dazed":
                return R.drawable.dazed;
            default:
                return 0;
        }
    }

    public String getMessage() {
        return isInitial ? "reacted" : "updated reaction";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reaction))
            return false;
        Reaction other = (Reaction) obj;
        return user.equals(other.user) && reactionType.equals(other.reactionType);
    }
}
