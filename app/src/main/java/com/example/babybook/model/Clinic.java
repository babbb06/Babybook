package com.example.babybook.model;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Clinic {
    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    private String clinicId;
    private String doctorId;
    private String doctorName;
    private Double latitude;
    private Double longitude;
    private long timestamp;
    private String profileImageUrl;
    private String clinicProfileUrl;
    private String specialization;
    private String clinicName;
    private String clinicPhoneNumber;
    private String schedStartTime;
    private String schedEndTime;
    private  String clinicAddress;
    private List<String> schedDays;
    private Map<String, Integer> vaccines;


    private boolean isExpanded;  // New field for expansion tracking

    public Clinic() {
        // Default constructor required for Firestore
    }

    public Clinic(String clinicId, String clinicName, String clinicPhoneNumber, String clinicProfileUrl, List<String> schedDays, String schedStartTime, String schedEndTime, String clinicAddress, String doctorId, String doctorName, Double latitude, Double longitude, long timestamp, String profileImageUrl, String specialization, Map<String, Integer> vaccines) {
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
        this.clinicProfileUrl = clinicProfileUrl;
        this.schedStartTime = schedStartTime;
        this.schedEndTime = schedEndTime;
        this.clinicAddress = clinicAddress;
        this.schedDays = schedDays;
        this.vaccines = vaccines != null ? vaccines : new HashMap<>();

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

    public String getSchedEndTime() {
        return schedEndTime;
    }

    public void setSchedEndTime(String schedEndTime) {
        this.schedEndTime = schedEndTime;
    }


    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }




    public String getSchedStartTime() {
        return schedStartTime;
    }

    public void setSchedStartTime(String schedStartTime) {
        this.schedStartTime = schedStartTime;
    }

    public List<String> getSchedDays() {
        return schedDays;
    }

    public void setSchedDays(List<String> schedDays) {
        this.schedDays = schedDays;
    }

    public Map<String, Integer> getVaccines() {
        return vaccines;
    }

    public void setVaccines(Map<String, Integer> vaccines) {
        this.vaccines = vaccines;
    }

    public String getClinicProfileUrl() {
        return clinicProfileUrl;
    }

    public void setClinicProfileUrl(String clinicProfileUrl) {
        this.clinicProfileUrl = clinicProfileUrl;
    }
}
