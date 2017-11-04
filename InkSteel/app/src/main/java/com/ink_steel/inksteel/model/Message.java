package com.ink_steel.inksteel.model;

public class Message {

    private String userName;
    private String message;
    private long time;

    public Message() {
    }

    public Message(String userName, String message, long time) {
        this.userName = userName;
        this.message = message;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }
}
