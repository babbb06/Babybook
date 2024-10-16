package com.example.babybook.model;

import java.util.Date;

public class AppointmentRequest {

    private String id;
    private String firstName;
    private String lastName;
    private String childFullName;
    private String birthDay;
    private String sex;
    private String birthPlace;
    private String address;
    private String service;
    private String date;
    private String time;
    private String status;
    private String userId;
    private String doctorId;
    private Date bookingTime;

    public AppointmentRequest() {
        // Default constructor required for Firestore serialization
    }

    public AppointmentRequest(String id, String firstName, String lastName,  String childFullName, String birthDay, String sex, String birthPlace, String address, String service, String date, String time, String status, String userId, String doctorId, Date bookingTime) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.childFullName = childFullName;
        this.birthDay = birthDay;
        this.sex = sex;
        this.birthPlace = birthPlace;
        this.address = address;
        this.service = service;
        this.date = date;
        this.time = time;
        this.status = status;
        this.userId = userId;
        this.doctorId = doctorId;
        this.bookingTime = bookingTime;
    }

    // Getter and Setter methods for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getChildFullName() {
        return childFullName;
    }

    public void setChildFullName(String childFullName) {
        this.childFullName = childFullName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        this.doctorId = doctorId;
    }

    public Date getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Date bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Child's Name: " + childFullName + "\n" +
                "Birth Date: " + birthDay + "\n" +
                "Birth Place: " + birthPlace + "\n" +
                "Address: " + address + "\n" +
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
