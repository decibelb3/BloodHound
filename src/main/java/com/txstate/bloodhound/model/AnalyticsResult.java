package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data transfer object containing aggregated analytics for UI presentation.
 */
public class AnalyticsResult {
    /** Mean systolic value across records with systolic measurements. */
    private Double averageSystolic;
    /** Mean diastolic value across records with diastolic measurements. */
    private Double averageDiastolic;
    /** Mean heart rate across records with heart-rate measurements. */
    private Double averageHeartRate;
    /** Mean total cholesterol across records with total-cholesterol measurements. */
    private Double averageTotalCholesterol;
    /** Mean LDL across records with LDL measurements. */
    private Double averageLdl;
    /** Mean HDL across records with HDL measurements. */
    private Double averageHdl;
    /** Mean triglycerides across records with triglycerides measurements. */
    private Double averageTriglycerides;
    /** Count of records by blood pressure category label. */
    private Map<String, Integer> bloodPressureCategoryCounts = new HashMap<>();
    /** Summary alert messages derived from aggregate risk indicators. */
    private List<String> alertSummaries = new ArrayList<>();
    /** Human-readable trend summary for recent versus prior records. */
    private String trendSummary;

    /**
     * Creates an empty analytics container.
     */
    public AnalyticsResult() {
    }

    /**
     * Returns average systolic value.
     *
     * @return average systolic, or {@code null} if unavailable
     */
    public Double getAverageSystolic() {
        return averageSystolic;
    }

    /**
     * Sets average systolic value.
     *
     * @param averageSystolic average systolic, or {@code null}
     */
    public void setAverageSystolic(Double averageSystolic) {
        this.averageSystolic = averageSystolic;
    }

    /**
     * Returns average diastolic value.
     *
     * @return average diastolic, or {@code null} if unavailable
     */
    public Double getAverageDiastolic() {
        return averageDiastolic;
    }

    /**
     * Sets average diastolic value.
     *
     * @param averageDiastolic average diastolic, or {@code null}
     */
    public void setAverageDiastolic(Double averageDiastolic) {
        this.averageDiastolic = averageDiastolic;
    }

    /**
     * Returns average heart rate.
     *
     * @return average heart rate, or {@code null} if unavailable
     */
    public Double getAverageHeartRate() {
        return averageHeartRate;
    }

    /**
     * Sets average heart rate.
     *
     * @param averageHeartRate average heart rate, or {@code null}
     */
    public void setAverageHeartRate(Double averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    /**
     * Returns average total cholesterol.
     *
     * @return average total cholesterol, or {@code null} if unavailable
     */
    public Double getAverageTotalCholesterol() {
        return averageTotalCholesterol;
    }

    /**
     * Sets average total cholesterol.
     *
     * @param averageTotalCholesterol average total cholesterol, or {@code null}
     */
    public void setAverageTotalCholesterol(Double averageTotalCholesterol) {
        this.averageTotalCholesterol = averageTotalCholesterol;
    }

    /**
     * Returns average LDL.
     *
     * @return average LDL, or {@code null} if unavailable
     */
    public Double getAverageLdl() {
        return averageLdl;
    }

    /**
     * Sets average LDL.
     *
     * @param averageLdl average LDL, or {@code null}
     */
    public void setAverageLdl(Double averageLdl) {
        this.averageLdl = averageLdl;
    }

    /**
     * Returns average HDL.
     *
     * @return average HDL, or {@code null} if unavailable
     */
    public Double getAverageHdl() {
        return averageHdl;
    }

    /**
     * Sets average HDL.
     *
     * @param averageHdl average HDL, or {@code null}
     */
    public void setAverageHdl(Double averageHdl) {
        this.averageHdl = averageHdl;
    }

    /**
     * Returns average triglycerides.
     *
     * @return average triglycerides, or {@code null} if unavailable
     */
    public Double getAverageTriglycerides() {
        return averageTriglycerides;
    }

    /**
     * Sets average triglycerides.
     *
     * @param averageTriglycerides average triglycerides, or {@code null}
     */
    public void setAverageTriglycerides(Double averageTriglycerides) {
        this.averageTriglycerides = averageTriglycerides;
    }

    /**
     * Returns category counts keyed by blood pressure category label.
     *
     * @return mutable map reference used by this DTO
     */
    public Map<String, Integer> getBloodPressureCategoryCounts() {
        return bloodPressureCategoryCounts;
    }

    /**
     * Replaces category counts keyed by blood pressure category label.
     *
     * @param bloodPressureCategoryCounts map of category labels to counts
     */
    public void setBloodPressureCategoryCounts(Map<String, Integer> bloodPressureCategoryCounts) {
        this.bloodPressureCategoryCounts = bloodPressureCategoryCounts;
    }

    /**
     * Returns aggregate alert summaries.
     *
     * @return list of summary alert lines
     */
    public List<String> getAlertSummaries() {
        return alertSummaries;
    }

    /**
     * Replaces aggregate alert summaries.
     *
     * @param alertSummaries list of summary alert lines
     */
    public void setAlertSummaries(List<String> alertSummaries) {
        this.alertSummaries = alertSummaries;
    }

    /**
     * Returns trend summary text.
     *
     * @return trend summary, or {@code null}
     */
    public String getTrendSummary() {
        return trendSummary;
    }

    /**
     * Sets trend summary text.
     *
     * @param trendSummary trend summary text, or {@code null}
     */
    public void setTrendSummary(String trendSummary) {
        this.trendSummary = trendSummary;
    }
}
