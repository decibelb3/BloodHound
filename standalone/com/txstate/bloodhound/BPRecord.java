package com.txstate.bloodhound;

public class BPRecord {
    private final String sessionId;
    private final long timestamp;
    private final int systolic;
    private final int diastolic;
    private final int heartRate;
    private final String timeOfDay;
    private final String medTiming;
    private final String activityTiming;
    private final int totalCholesterol;
    private final int ldl;
    private final int hdl;

    public BPRecord(String sessionId, long timestamp, int systolic, int diastolic,
                    int heartRate, String timeOfDay, String medTiming, String activityTiming) {
        this(sessionId, timestamp, systolic, diastolic, heartRate, timeOfDay, medTiming, activityTiming, 0, 0, 0);
    }

    public BPRecord(String sessionId, long timestamp, int systolic, int diastolic,
                    int heartRate, String timeOfDay, String medTiming, String activityTiming,
                    int totalCholesterol, int ldl, int hdl) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.timeOfDay = timeOfDay;
        this.medTiming = medTiming;
        this.activityTiming = activityTiming;
        this.totalCholesterol = totalCholesterol;
        this.ldl = ldl;
        this.hdl = hdl;
    }

    public String getAHACategory() {
        if (systolic > 180 || diastolic > 120) return "Crisis";
        if (systolic >= 140 || diastolic >= 90) return "Stage 2";
        if (systolic >= 130 || diastolic >= 80) return "Stage 1";
        if (systolic >= 120 && diastolic < 80) return "Elevated";
        return "Normal";
    }

    public String getSessionId() { return sessionId; }
    public long getTimestamp() { return timestamp; }
    public int getSystolic() { return systolic; }
    public int getDiastolic() { return diastolic; }
    public int getHeartRate() { return heartRate; }
    public String getTimeOfDay() { return timeOfDay; }
    public String getMedTiming() { return medTiming; }
    public String getActivityTiming() { return activityTiming; }
    public int getTotalCholesterol() { return totalCholesterol; }
    public int getLdl() { return ldl; }
    public int getHdl() { return hdl; }
}
