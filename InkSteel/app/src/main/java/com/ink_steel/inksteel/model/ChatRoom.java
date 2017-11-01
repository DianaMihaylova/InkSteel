package com.ink_steel.inksteel.model;

class ChatRoom {
    private String user1Email;
    private String user1ProfileImage;
    private String user2Email;
    private String user2ProfileImage;
    private String lastMessage;
    private long lastMessageTime;

    public ChatRoom() {
    }

    public ChatRoom(String user1Email, String user1ProfileImage, String user2Email,
                    String user2ProfileImage, String lastMessage, long lastMessageTime) {
        this.user1Email = user1Email;
        this.user1ProfileImage = user1ProfileImage;
        this.user2Email = user2Email;
        this.user2ProfileImage = user2ProfileImage;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUser1Email() {
        return user1Email;
    }

    public String getUser1ProfileImage() {
        return user1ProfileImage;
    }

    public String getUser2Email() {
        return user2Email;
    }

    public String getUser2ProfileImage() {
        return user2ProfileImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }
}
