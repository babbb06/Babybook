package com.example.babybook.model;

public class Appointment {
    private String userId;
    private String doctorId;
    private String date;
    private String time;

    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String userId, String doctorId, String date, String time) {
        this.userId = userId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
