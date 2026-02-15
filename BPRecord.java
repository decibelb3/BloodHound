package com.txstate.bloodhound;

public class BPRecord {
    private String sessionId;
    private long timestamp;
    private int systolic;
    private int diastolic;
    private int heartRate;
    private String timeOfDay;
    private String medTiming;
    private String activityTiming;

    public BPRecord(String sessionId, long timestamp, int systolic, int diastolic, 
                    int heartRate, String timeOfDay, String medTiming, String activityTiming) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.timeOfDay = timeOfDay;
        this.medTiming = medTiming;
        this.activityTiming = activityTiming;
    }

    // AHA Category Logic (FR-020)
    public String getAHACategory() {
        if (systolic > 180 || diastolic > 120) return "Crisis";
        if (systolic >= 140 || diastolic >= 90) return "Stage 2";
        if (systolic >= 130 || diastolic >= 80) return "Stage 1";
        if (systolic >= 120 && diastolic < 80) return "Elevated";
        return "Normal";
    }

    // Getters for Database and CSV Export
    public String getSessionId() { return sessionId; }
    public long getTimestamp() { return timestamp; }
    public int getSystolic() { return systolic; }
    public int getDiastolic() { return diastolic; }
    public int getHeartRate() { return heartRate; }
    public String getTimeOfDay() { return timeOfDay; }
    public String getMedTiming() { return medTiming; }
    public String getActivityTiming() { return activityTiming; }
}