package com.example.babybook.model;

import java.util.Date;
import java.util.List;

public class Doctor {
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String birthday; // Assuming you want to store the birthday as a Date object
    private String email;
    private String phoneNumber;
    private String PRCLicenseNumber;
    private String specialization;
    private String clinicAddress;
    private String schedStartTime;
    private String schedEndTime;
    private List<String> schedDays;





    // Getters and setters

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPRCLicenseNumber() {
        return PRCLicenseNumber;
    }

    public void setPRCLicenseNumber(String PRCLicenseNumber) {
        this.PRCLicenseNumber = PRCLicenseNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }


    public void setSchedDays(List<String> schedDays) {
        this.schedDays = schedDays;
    }

    public String getSchedEndTime() {
        return schedEndTime;
    }

    public void setSchedEndTime(String schedEndTime) {
        this.schedEndTime = schedEndTime;
    }

    public String getSchedStartTime() {
        return schedStartTime;
    }
    public List<String> getSchedDays() {
        return schedDays;
    }

    public void setSchedStartTime(String schedStartTime) {
        this.schedStartTime = schedStartTime;
    }
}
