package com.txstate.bloodhound.model;

/**
 * Stores a single health record and its derived classifications.
 */
public class HealthRecord {
    private String sessionId;
    private long timestampEpochMillis;
    private Integer systolic;
    private Integer diastolic;
    private Integer heartRate;
    private Integer totalCholesterol;
    private Integer ldl;
    private Integer hdl;
    private Integer triglycerides;
    private String timeOfDay;
    private String medTiming;
    private String activityTiming;
    private String bloodPressureCategory;
    private String totalCholesterolCategory;
    private String ldlCategory;
    private String hdlCategory;
    private String triglyceridesCategory;
    private String lipidSummary;

    public HealthRecord() {
        // Default constructor for loading/parsing.
    }

    public HealthRecord(Integer systolic, Integer diastolic, Integer heartRate,
                        Integer totalCholesterol, Integer ldl, Integer hdl, Integer triglycerides,
                        String timeOfDay, String medTiming, String activityTiming) {
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.totalCholesterol = totalCholesterol;
        this.ldl = ldl;
        this.hdl = hdl;
        this.triglycerides = triglycerides;
        this.timeOfDay = timeOfDay;
        this.medTiming = medTiming;
        this.activityTiming = activityTiming;
    }

    public boolean hasAtLeastOneMetric() {
        return systolic != null
                || diastolic != null
                || heartRate != null
                || totalCholesterol != null
                || ldl != null
                || hdl != null
                || triglycerides != null;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getTimestampEpochMillis() {
        return timestampEpochMillis;
    }

    public void setTimestampEpochMillis(long timestampEpochMillis) {
        this.timestampEpochMillis = timestampEpochMillis;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getTotalCholesterol() {
        return totalCholesterol;
    }

    public void setTotalCholesterol(Integer totalCholesterol) {
        this.totalCholesterol = totalCholesterol;
    }

    public Integer getLdl() {
        return ldl;
    }

    public void setLdl(Integer ldl) {
        this.ldl = ldl;
    }

    public Integer getHdl() {
        return hdl;
    }

    public void setHdl(Integer hdl) {
        this.hdl = hdl;
    }

    public Integer getTriglycerides() {
        return triglycerides;
    }

    public void setTriglycerides(Integer triglycerides) {
        this.triglycerides = triglycerides;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getMedTiming() {
        return medTiming;
    }

    public void setMedTiming(String medTiming) {
        this.medTiming = medTiming;
    }

    public String getActivityTiming() {
        return activityTiming;
    }

    public void setActivityTiming(String activityTiming) {
        this.activityTiming = activityTiming;
    }

    public String getBloodPressureCategory() {
        return bloodPressureCategory;
    }

    public void setBloodPressureCategory(String bloodPressureCategory) {
        this.bloodPressureCategory = bloodPressureCategory;
    }

    public String getTotalCholesterolCategory() {
        return totalCholesterolCategory;
    }

    public void setTotalCholesterolCategory(String totalCholesterolCategory) {
        this.totalCholesterolCategory = totalCholesterolCategory;
    }

    public String getLdlCategory() {
        return ldlCategory;
    }

    public void setLdlCategory(String ldlCategory) {
        this.ldlCategory = ldlCategory;
    }

    public String getHdlCategory() {
        return hdlCategory;
    }

    public void setHdlCategory(String hdlCategory) {
        this.hdlCategory = hdlCategory;
    }

    public String getTriglyceridesCategory() {
        return triglyceridesCategory;
    }

    public void setTriglyceridesCategory(String triglyceridesCategory) {
        this.triglyceridesCategory = triglyceridesCategory;
    }

    public String getLipidSummary() {
        return lipidSummary;
    }

    public void setLipidSummary(String lipidSummary) {
        this.lipidSummary = lipidSummary;
    }
}
