package com.example.babybook.model;

public class HealthRecord {
    private String childName;
    private String addedBy; // User ID or name of who added the record
    private String id; // To store document ID

    // Default constructor required for Firestore
    public HealthRecord() {}

    public HealthRecord(String childName, String addedBy) {
        this.childName = childName;
        this.addedBy = addedBy;
    }

    public String getChildName() { return childName; }
    public String getAddedBy() { return addedBy; }
    public String getId() { return id; }

    public void setChildName(String childName) { this.childName = childName; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "childName='" + childName + '\'' +
                ", addedBy='" + addedBy + '\'' +
                '}';
    }
}
