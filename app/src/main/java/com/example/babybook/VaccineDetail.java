package com.example.babybook;

public class VaccineDetail {
    private String vaccineName;
    private String dose;
    private String type;
    private String location;
    private String date;
    private String reaction;

    public VaccineDetail() {
        // N
    }

    public VaccineDetail(String vaccineName, String dose, String type, String location, String date, String reaction) {
        this.vaccineName = vaccineName;
        this.dose = dose;
        this.type = type;
        this.location = location;
        this.date = date;
        this.reaction = reaction;
    }

    // Getters and setters
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
}
