package com.ink_steel.inksteel.model;

import com.google.firebase.firestore.Exclude;

public class ChatRoom {
    private String email1;
    private String userName1;
    private String profilePicture1;
    private String email2;
    private String userName2;
    private String profilePicture2;
    private String lastMessage;
    private long lastMessageTime;
    private String chatId;

    public ChatRoom() {
    }

    public ChatRoom(String chatId, String email1, String profilePicture1, String userName1,
                    String email2, String profilePicture2, String userName2,
                    String lastMessage, long lastMessageTime) {
        this.email1 = email1;
        this.profilePicture1 = profilePicture1;
        this.userName1 = userName1;
        this.email2 = email2;
        this.profilePicture2 = profilePicture2;
        this.userName2 = userName2;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatId() {
        return chatId;
    }

    public String getEmail1() {
        return email1;
    }

    public String getProfilePicture1() {
        return profilePicture1;
    }

    public String getEmail2() {
        return email2;
    }

    public String getProfilePicture2() {
        return profilePicture2;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public String getUserName1() {
        return userName1;
    }

    public String getUserName2() {
        return userName2;
    }

    @Exclude
    public String getOtherUser(String query) {
        switch (query) {
            case "email1":
                return email2;
            case "email2":
                return email1;
        }
        return null;
    }

    @Exclude
    public String getOtherProfilePicture(String email) {
        if (email.equals(email1))
            return profilePicture2;
        else return profilePicture1;
    }

    @Exclude
    public String getOtherUserName(String email) {
        if (email.equals(email1))
            return userName2;
        else return userName1;
    }
}
