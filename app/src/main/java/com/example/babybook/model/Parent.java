package com.example.babybook.model;

public class Parent {

    private String parentID;
    private String email;
    private String fullName;

    public Parent() {
        // Default constructor required for Firestore
    }

    public Parent(String parentID, String email, String fullName) {
        this.parentID = parentID;
        this.email = email;
        this.fullName = fullName;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
