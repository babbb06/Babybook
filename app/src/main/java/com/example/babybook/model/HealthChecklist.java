package com.example.babybook.model;


import com.google.firebase.Timestamp;

public class HealthChecklist {

    private String date;
    private String weight;
    private String temperature;
    private String sex;
    private String address;

    private boolean isSick;
    private boolean hasCough;
    private boolean hasDiarrhea;
    private boolean hasFever;
    private boolean hasMeasles;
    private boolean hasEarPain;
    private boolean hasPallor;
    private boolean isMalnourished;
    private boolean assessFeeding;
    private boolean assessBreastfeeding;
    private boolean hasLongDiarrheaOrCough;
    private boolean needsImmunization;
    private boolean hasOtherProblems;


    private String summaryDiagnosis;
    private String treatmentPlan;
    private String followUpPlan;


    private String LastName;
    private String FirstName;


    private String doctorId;
    private String uniqueId;
    private String datetoday;

    private String parentId;       // ID of the parent
    private String childId;        // ID of the child
    private Timestamp timestamp;
    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }




    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }




    public String getDateToday() {
        return datetoday;
    }

    public void setDateToday(String dateToday) {
        this.datetoday = dateToday;
    }






    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return FirstName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }








    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public boolean isSick() {
        return isSick;
    }


    public void setSick(boolean sick) {
        isSick = sick;
    }



    public boolean hasCough() {
        return hasCough;
    }

    public void setCough(boolean cough) {
        hasCough = cough;
    }

    public boolean hasDiarrhea() {
        return hasDiarrhea;
    }

    public void setDiarrhea(boolean diarrhea) {
        hasDiarrhea = diarrhea;
    }

    public boolean hasFever() {
        return hasFever;
    }

    public void setFever(boolean fever) {
        hasFever = fever;
    }

    public boolean hasMeasles() {
        return hasMeasles;
    }

    public void setMeasles(boolean measles) {
        hasMeasles = measles;
    }

    public boolean hasEarPain() {
        return hasEarPain;
    }

    public void setEarPain(boolean earPain) {
        hasEarPain = earPain;
    }

    public boolean hasPallor() {
        return hasPallor;
    }

    public void setPallor(boolean pallor) {
        hasPallor = pallor;
    }

    public boolean isMalnourished() {
        return isMalnourished;
    }

    public void setMalnourished(boolean malnourished) {
        isMalnourished = malnourished;
    }

    public boolean isAssessFeeding() {
        return assessFeeding;
    }

    public void setAssessFeeding(boolean assessFeeding) {
        this.assessFeeding = assessFeeding;
    }

    public boolean isAssessBreastfeeding() {
        return assessBreastfeeding;
    }

    public void setAssessBreastfeeding(boolean assessBreastfeeding) {
        this.assessBreastfeeding = assessBreastfeeding;
    }

    public boolean hasLongDiarrheaOrCough() {
        return hasLongDiarrheaOrCough;
    }

    public void setLongDiarrheaOrCough(boolean longDiarrheaOrCough) {
        hasLongDiarrheaOrCough = longDiarrheaOrCough;
    }

    public boolean needsImmunization() {
        return needsImmunization;
    }

    public void setNeedsImmunization(boolean needsImmunization) {
        this.needsImmunization = needsImmunization;
    }

    public boolean hasOtherProblems() {
        return hasOtherProblems;
    }

    public void setOtherProblems(boolean otherProblems) {
        hasOtherProblems = otherProblems;
    }

    public String getSummaryDiagnosis() {
        return summaryDiagnosis;
    }

    public void setSummaryDiagnosis(String summaryDiagnosis) {
        this.summaryDiagnosis = summaryDiagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getFollowUpPlan() {
        return followUpPlan;
    }

    public void setFollowUpPlan(String followUpPlan) {
        this.followUpPlan = followUpPlan;
    }


    // New getters and setters for parentId, childId, and timestamp
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
