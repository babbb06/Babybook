package com.example.babybook.model;

import com.google.android.gms.maps.model.LatLng;

public class Clinic {
    private String clinicId;
    private String doctorId;
    private String doctorName;
    private Double latitude;
    private Double longitude;
    private long timestamp;
    private String profileImageUrl;
    private String specialization;
    private String clinicName;
    private String clinicPhoneNumber;

    private boolean isExpanded;  // New field for expansion tracking

    public Clinic() {
        // Default constructor required for Firestore
    }

    public Clinic(String clinicId, String clinicName, String clinicPhoneNumber, String doctorId, String doctorName, Double latitude, Double longitude, long timestamp, String profileImageUrl, String specialization) {
        this.clinicId = clinicId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.latitude = latitude; // Assign latitude
        this.longitude = longitude; // Assign longitude
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
        this.specialization = specialization;
        this.clinicName = clinicName;
        this.clinicPhoneNumber = clinicPhoneNumber;
    }

    /*public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getPostId() {
        return clinicId;
    }

    public void setPostId(String postId) {
        this.clinicId = postId;
    } */

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
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


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getClinicPhoneNumber() {
        return clinicPhoneNumber;
    }

    public void setClinicPhoneNumber(String clinicPhoneNumber) {
        this.clinicPhoneNumber = clinicPhoneNumber;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }
}
