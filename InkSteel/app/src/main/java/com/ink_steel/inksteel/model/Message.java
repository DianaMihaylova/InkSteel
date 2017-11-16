package com.ink_steel.inksteel.model;

public class Message {

    private String userEmail;
    private String message;
    private long time;
    private boolean seen;

    public Message() {
    }

    public Message(String userEmail, String message, long time, boolean seen) {
        this.userEmail = userEmail;
        this.message = message;
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
