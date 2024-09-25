package com.example.babybook.model;

public class Post {
    private String postId;
    private String doctorId;
    private String doctorName;
    private String content;
    private long timestamp;
    private String profileImageUrl;
    private String specialization;

    public Post() {
        // Default constructor required for Firestore
    }

    public Post(String postId, String doctorId, String doctorName, String content, long timestamp, String profileImageUrl, String specialization) {
        this.postId = postId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.content = content;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
        this.specialization = specialization;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
