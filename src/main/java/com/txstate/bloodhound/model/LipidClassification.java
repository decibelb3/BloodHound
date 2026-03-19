package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds lipid classification labels and generated alerts.
 */
public class LipidClassification {
    private String totalCholesterolCategory;
    private String ldlCategory;
    private String hdlCategory;
    private String triglyceridesCategory;
    private String summary;
    private List<String> alerts = new ArrayList<>();

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts == null ? new ArrayList<>() : new ArrayList<>(alerts);
    }
}
