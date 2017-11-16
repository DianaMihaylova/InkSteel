package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;

public class ChatRoom {

    private String email;
    private String userName;
    private String profilePicture;
    private String lastMessage;
    private long lastMessageTime;
    private String lastMessageSender;
    private String chatId;
    private boolean seen;

    public ChatRoom() {
    }

    public ChatRoom(String chatId, String email, String profilePicture, String userName,
                    String lastMessage, long lastMessageTime, String lastMessageSender,
                    boolean seen) {
        this.chatId = chatId;
        this.email = email;
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSender = lastMessageSender;
        this.seen = seen;
    }

    public boolean isSeen() {
        return seen;
    }

    public String getChatId() {
        return chatId;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    @Exclude
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChatRoom && chatId.equals(((ChatRoom) obj).chatId);
    }

    public void makeSeen() {
        seen = true;
    }

    public void setInfo(ChatRoom chatRoom) {
        this.lastMessage = chatRoom.lastMessage;
        this.lastMessageTime = chatRoom.lastMessageTime;
        this.lastMessageSender = chatRoom.lastMessageSender;
        this.seen = chatRoom.seen;
    }
}
