package com.txstate.bloodhound.model;

/**
 * Represents a single captured health session and its derived classifications.
 * <p>
 * A record stores raw metrics (blood pressure, heart rate, and lipid values), contextual
 * tags (time of day and timing context), and calculated category labels assigned by the
 * service layer.
 * </p>
 */
public class HealthRecord {
    /** Unique identifier assigned when a record is persisted. */
    private String sessionId;
    /** Unix timestamp in milliseconds indicating when the record was stored. */
    private long timestampEpochMillis;
    /** Systolic blood pressure value in mmHg, or {@code null} when omitted. */
    private Integer systolic;
    /** Diastolic blood pressure value in mmHg, or {@code null} when omitted. */
    private Integer diastolic;
    /** Heart rate value in beats per minute, or {@code null} when omitted. */
    private Integer heartRate;
    /** Total cholesterol value in mg/dL, or {@code null} when omitted. */
    private Integer totalCholesterol;
    /** LDL value in mg/dL, or {@code null} when omitted. */
    private Integer ldl;
    /** HDL value in mg/dL, or {@code null} when omitted. */
    private Integer hdl;
    /** Triglycerides value in mg/dL, or {@code null} when omitted. */
    private Integer triglycerides;
    /** Context label describing approximate time of day for the reading. */
    private String timeOfDay;
    /** Context label describing medication timing around the reading. */
    private String medTiming;
    /** Context label describing activity timing around the reading. */
    private String activityTiming;
    /** Derived blood pressure category label. */
    private String bloodPressureCategory;
    /** Derived total cholesterol category label. */
    private String totalCholesterolCategory;
    /** Derived LDL category label. */
    private String ldlCategory;
    /** Derived HDL category label. */
    private String hdlCategory;
    /** Derived triglycerides category label. */
    private String triglyceridesCategory;
    /** Human-readable summary of lipid alert status. */
    private String lipidSummary;

    /**
     * Creates an empty record for JSON loading and incremental field population.
     */
    public HealthRecord() {
        // Default constructor for loading/parsing.
    }

    /**
     * Creates a draft health record with metric and context values.
     *
     * @param systolic systolic blood pressure in mmHg
     * @param diastolic diastolic blood pressure in mmHg
     * @param heartRate heart rate in bpm
     * @param totalCholesterol total cholesterol in mg/dL
     * @param ldl LDL in mg/dL
     * @param hdl HDL in mg/dL
     * @param triglycerides triglycerides in mg/dL
     * @param timeOfDay time-of-day context label
     * @param medTiming medication timing context label
     * @param activityTiming activity timing context label
     */
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

    /**
     * Indicates whether at least one quantitative metric is present.
     *
     * @return {@code true} when any metric field is non-null; otherwise {@code false}
     */
    public boolean hasAtLeastOneMetric() {
        return systolic != null
                || diastolic != null
                || heartRate != null
                || totalCholesterol != null
                || ldl != null
                || hdl != null
                || triglycerides != null;
    }

    /**
     * Returns the persisted session identifier.
     *
     * @return session identifier, or {@code null} before persistence
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the persisted session identifier.
     *
     * @param sessionId unique session identifier
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns the stored timestamp in epoch milliseconds.
     *
     * @return epoch milliseconds
     */
    public long getTimestampEpochMillis() {
        return timestampEpochMillis;
    }

    /**
     * Sets the stored timestamp in epoch milliseconds.
     *
     * @param timestampEpochMillis epoch milliseconds
     */
    public void setTimestampEpochMillis(long timestampEpochMillis) {
        this.timestampEpochMillis = timestampEpochMillis;
    }

    /**
     * Returns systolic blood pressure.
     *
     * @return systolic value in mmHg, or {@code null}
     */
    public Integer getSystolic() {
        return systolic;
    }

    /**
     * Sets systolic blood pressure.
     *
     * @param systolic systolic value in mmHg, or {@code null}
     */
    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    /**
     * Returns diastolic blood pressure.
     *
     * @return diastolic value in mmHg, or {@code null}
     */
    public Integer getDiastolic() {
        return diastolic;
    }

    /**
     * Sets diastolic blood pressure.
     *
     * @param diastolic diastolic value in mmHg, or {@code null}
     */
    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    /**
     * Returns heart rate.
     *
     * @return heart rate in bpm, or {@code null}
     */
    public Integer getHeartRate() {
        return heartRate;
    }

    /**
     * Sets heart rate.
     *
     * @param heartRate heart rate in bpm, or {@code null}
     */
    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    /**
     * Returns total cholesterol.
     *
     * @return total cholesterol in mg/dL, or {@code null}
     */
    public Integer getTotalCholesterol() {
        return totalCholesterol;
    }

    /**
     * Sets total cholesterol.
     *
     * @param totalCholesterol total cholesterol in mg/dL, or {@code null}
     */
    public void setTotalCholesterol(Integer totalCholesterol) {
        this.totalCholesterol = totalCholesterol;
    }

    /**
     * Returns LDL.
     *
     * @return LDL in mg/dL, or {@code null}
     */
    public Integer getLdl() {
        return ldl;
    }

    /**
     * Sets LDL.
     *
     * @param ldl LDL in mg/dL, or {@code null}
     */
    public void setLdl(Integer ldl) {
        this.ldl = ldl;
    }

    /**
     * Returns HDL.
     *
     * @return HDL in mg/dL, or {@code null}
     */
    public Integer getHdl() {
        return hdl;
    }

    /**
     * Sets HDL.
     *
     * @param hdl HDL in mg/dL, or {@code null}
     */
    public void setHdl(Integer hdl) {
        this.hdl = hdl;
    }

    /**
     * Returns triglycerides.
     *
     * @return triglycerides in mg/dL, or {@code null}
     */
    public Integer getTriglycerides() {
        return triglycerides;
    }

    /**
     * Sets triglycerides.
     *
     * @param triglycerides triglycerides in mg/dL, or {@code null}
     */
    public void setTriglycerides(Integer triglycerides) {
        this.triglycerides = triglycerides;
    }

    /**
     * Returns time-of-day context.
     *
     * @return time-of-day label, or {@code null}
     */
    public String getTimeOfDay() {
        return timeOfDay;
    }

    /**
     * Sets time-of-day context.
     *
     * @param timeOfDay time-of-day label, or {@code null}
     */
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    /**
     * Returns medication timing context.
     *
     * @return medication timing label, or {@code null}
     */
    public String getMedTiming() {
        return medTiming;
    }

    /**
     * Sets medication timing context.
     *
     * @param medTiming medication timing label, or {@code null}
     */
    public void setMedTiming(String medTiming) {
        this.medTiming = medTiming;
    }

    /**
     * Returns activity timing context.
     *
     * @return activity timing label, or {@code null}
     */
    public String getActivityTiming() {
        return activityTiming;
    }

    /**
     * Sets activity timing context.
     *
     * @param activityTiming activity timing label, or {@code null}
     */
    public void setActivityTiming(String activityTiming) {
        this.activityTiming = activityTiming;
    }

    /**
     * Returns derived blood pressure category.
     *
     * @return blood pressure category label, or {@code null}
     */
    public String getBloodPressureCategory() {
        return bloodPressureCategory;
    }

    /**
     * Sets derived blood pressure category.
     *
     * @param bloodPressureCategory blood pressure category label, or {@code null}
     */
    public void setBloodPressureCategory(String bloodPressureCategory) {
        this.bloodPressureCategory = bloodPressureCategory;
    }

    /**
     * Returns derived total cholesterol category.
     *
     * @return total cholesterol category label, or {@code null}
     */
    public String getTotalCholesterolCategory() {
        return totalCholesterolCategory;
    }

    /**
     * Sets derived total cholesterol category.
     *
     * @param totalCholesterolCategory total cholesterol category label, or {@code null}
     */
    public void setTotalCholesterolCategory(String totalCholesterolCategory) {
        this.totalCholesterolCategory = totalCholesterolCategory;
    }

    /**
     * Returns derived LDL category.
     *
     * @return LDL category label, or {@code null}
     */
    public String getLdlCategory() {
        return ldlCategory;
    }

    /**
     * Sets derived LDL category.
     *
     * @param ldlCategory LDL category label, or {@code null}
     */
    public void setLdlCategory(String ldlCategory) {
        this.ldlCategory = ldlCategory;
    }

    /**
     * Returns derived HDL category.
     *
     * @return HDL category label, or {@code null}
     */
    public String getHdlCategory() {
        return hdlCategory;
    }

    /**
     * Sets derived HDL category.
     *
     * @param hdlCategory HDL category label, or {@code null}
     */
    public void setHdlCategory(String hdlCategory) {
        this.hdlCategory = hdlCategory;
    }

    /**
     * Returns derived triglycerides category.
     *
     * @return triglycerides category label, or {@code null}
     */
    public String getTriglyceridesCategory() {
        return triglyceridesCategory;
    }

    /**
     * Sets derived triglycerides category.
     *
     * @param triglyceridesCategory triglycerides category label, or {@code null}
     */
    public void setTriglyceridesCategory(String triglyceridesCategory) {
        this.triglyceridesCategory = triglyceridesCategory;
    }

    /**
     * Returns summary text describing lipid alert state.
     *
     * @return lipid summary string, or {@code null}
     */
    public String getLipidSummary() {
        return lipidSummary;
    }

    /**
     * Sets summary text describing lipid alert state.
     *
     * @param lipidSummary lipid summary text, or {@code null}
     */
    public void setLipidSummary(String lipidSummary) {
        this.lipidSummary = lipidSummary;
    }
}
