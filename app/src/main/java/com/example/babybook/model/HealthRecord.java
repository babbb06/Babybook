package com.example.babybook.model;

public class HealthRecord {
    private String childName;
    private String addedBy; // User ID or name of who added the record
    private String id;
    private String date;
    private String doctorId;
    private String service;
    private String time;
    private String status;
    private String userId; // Corrected the variable name to be more consistent

    // Default constructor required for Firestore
    public HealthRecord() {}

    public HealthRecord(String childName, String addedBy) {
        this.childName = childName;
        this.addedBy = addedBy;
    }

    public String getChildName() { return childName; }
    public String getAddedBy() { return addedBy; }
    public String getId() { return id; }
    public String getDate() { return date; }
    public String getDoctorId() { return doctorId; }
    public String getService() { return service; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getUserId() { return userId; }

    public void setChildName(String childName) { this.childName = childName; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }
    public void setId(String id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public void setService(String service) { this.service = service; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "childName='" + childName + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", service='" + service + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}