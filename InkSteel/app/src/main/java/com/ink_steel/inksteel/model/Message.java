package com.ink_steel.inksteel.model;

public class Message {

    private String userEmail;
    private String message;
    private long time;

    public Message() {
    }

    public Message(String userEmail, String message, long time) {
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
}
