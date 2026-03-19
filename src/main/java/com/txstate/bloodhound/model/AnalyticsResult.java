package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores computed analytics values for UI presentation.
 */
public class AnalyticsResult {
    private Double averageSystolic;
    private Double averageDiastolic;
    private Double averageHeartRate;
    private Double averageTotalCholesterol;
    private Double averageLdl;
    private Double averageHdl;
    private Double averageTriglycerides;
    private Map<String, Integer> bloodPressureCategoryCounts = new HashMap<>();
    private List<String> alertSummaries = new ArrayList<>();
    private String trendSummary;

    public Double getAverageSystolic() {
        return averageSystolic;
    }

    public void setAverageSystolic(Double averageSystolic) {
        this.averageSystolic = averageSystolic;
    }

    public Double getAverageDiastolic() {
        return averageDiastolic;
    }

    public void setAverageDiastolic(Double averageDiastolic) {
        this.averageDiastolic = averageDiastolic;
    }

    public Double getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(Double averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Double getAverageTotalCholesterol() {
        return averageTotalCholesterol;
    }

    public void setAverageTotalCholesterol(Double averageTotalCholesterol) {
        this.averageTotalCholesterol = averageTotalCholesterol;
    }

    public Double getAverageLdl() {
        return averageLdl;
    }

    public void setAverageLdl(Double averageLdl) {
        this.averageLdl = averageLdl;
    }

    public Double getAverageHdl() {
        return averageHdl;
    }

    public void setAverageHdl(Double averageHdl) {
        this.averageHdl = averageHdl;
    }

    public Double getAverageTriglycerides() {
        return averageTriglycerides;
    }

    public void setAverageTriglycerides(Double averageTriglycerides) {
        this.averageTriglycerides = averageTriglycerides;
    }

    public Map<String, Integer> getBloodPressureCategoryCounts() {
        return bloodPressureCategoryCounts;
    }

    public void setBloodPressureCategoryCounts(Map<String, Integer> bloodPressureCategoryCounts) {
        this.bloodPressureCategoryCounts = bloodPressureCategoryCounts;
    }

    public List<String> getAlertSummaries() {
        return alertSummaries;
    }

    public void setAlertSummaries(List<String> alertSummaries) {
        this.alertSummaries = alertSummaries;
    }

    public String getTrendSummary() {
        return trendSummary;
    }

    public void setTrendSummary(String trendSummary) {
        this.trendSummary = trendSummary;
    }
}
