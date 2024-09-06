package com.example.babybook.model;

import java.util.Date;

public class AppointmentRequest {

    private String id;
    private String childName;
    private String service;
    private String date;
    private String time;
    private String status;
    private String userId;
    private String doctorId; // Added field for doctor ID
    private Date bookingTime;

    public AppointmentRequest() {
        // Default constructor required for Firestore serialization
    }

    public AppointmentRequest(String id, String childName, String service, String date, String time, String status, String userId, String doctorId, Date bookingTime) {
        this.id = id;
        this.childName = childName;
        this.service = service;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userId = userId;
        this.doctorId = doctorId; // Initialize doctor ID
        this.bookingTime = bookingTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId; // Getter and setter for doctor ID
    }

    public Date getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Date bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Child's Name: " + childName + "\n" +
                "Service: " + service + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n" +
                "Status: " + status + "\n" +
                "Booked: " + formatTimeAgo(bookingTime);
    }

    private String formatTimeAgo(Date bookingTime) {
        if (bookingTime == null) return "Unknown time";

        long currentTime = System.currentTimeMillis();
        long bookingTimeMillis = bookingTime.getTime();
        long timeDifference = currentTime - bookingTimeMillis;

        long seconds = timeDifference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + " day(s) ago";
        if (hours > 0) return hours + " hour(s) ago";
        if (minutes > 0) return minutes + " minute(s) ago";
        return seconds + " second(s) ago";
    }
}
