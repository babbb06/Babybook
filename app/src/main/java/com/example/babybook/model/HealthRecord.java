package com.example.babybook.model;

public class HealthRecord {

    private String addedBy;   // User ID or name of who added the record
    private String id;
    private String firstName;
    private String lastName;
    private String birthDay;
    private String birthPlace;
    private String address;
    private String date;
    private String doctorId;
    private String service;
    private String time;
    private String status;
    private String userId;

    // Default constructor required for Firestore
    public HealthRecord() {}

    // Constructor with all fields
    public HealthRecord(String id, String firstName, String lastName, String birthDay, String birthPlace, String address, String addedBy, String date, String doctorId, String service, String time, String status, String userId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDay = birthDay;
        this.birthPlace = birthPlace;
        this.address = address;
        this.addedBy = addedBy;
        this.date = date;
        this.doctorId = doctorId;
        this.service = service;
        this.time = time;
        this.status = status;
        this.userId = userId;
    }

    // Getter and Setter methods
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

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    @Override
    public String toString() {
        return "HealthRecord{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", birthPlace='" + birthPlace + '\'' +
                ", address='" + address + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", date='" + date + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", service='" + service + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
