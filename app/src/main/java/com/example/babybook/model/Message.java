package com.example.babybook.model;

import java.util.Date;

public class Message {
    private String senderId;
    private String receiverId;
    private String message;
    private Date timestamp;
    private String senderName;

    public Message() {
        // Required empty constructor for Firestore serialization
    }

    public Message(String senderId, String receiverId, String message, Date timestamp, String senderName) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }
}