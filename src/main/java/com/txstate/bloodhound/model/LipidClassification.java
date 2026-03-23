package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds lipid classification labels and generated risk alerts for a record.
 */
public class LipidClassification {
    /** Derived category for total cholesterol. */
    private String totalCholesterolCategory;
    /** Derived category for LDL. */
    private String ldlCategory;
    /** Derived category for HDL. */
    private String hdlCategory;
    /** Derived category for triglycerides. */
    private String triglyceridesCategory;
    /** Human-readable one-line summary of lipid risk state. */
    private String summary;
    /** Detailed lipid alert messages for user feedback. */
    private List<String> alerts = new ArrayList<>();

    /**
     * Returns total cholesterol category.
     *
     * @return total cholesterol category label, or {@code null}
     */
    public String getTotalCholesterolCategory() {
        return totalCholesterolCategory;
    }

    /**
     * Sets total cholesterol category.
     *
     * @param totalCholesterolCategory category label, or {@code null}
     */
    public void setTotalCholesterolCategory(String totalCholesterolCategory) {
        this.totalCholesterolCategory = totalCholesterolCategory;
    }

    /**
     * Returns LDL category.
     *
     * @return LDL category label, or {@code null}
     */
    public String getLdlCategory() {
        return ldlCategory;
    }

    /**
     * Sets LDL category.
     *
     * @param ldlCategory category label, or {@code null}
     */
    public void setLdlCategory(String ldlCategory) {
        this.ldlCategory = ldlCategory;
    }

    /**
     * Returns HDL category.
     *
     * @return HDL category label, or {@code null}
     */
    public String getHdlCategory() {
        return hdlCategory;
    }

    /**
     * Sets HDL category.
     *
     * @param hdlCategory category label, or {@code null}
     */
    public void setHdlCategory(String hdlCategory) {
        this.hdlCategory = hdlCategory;
    }

    /**
     * Returns triglycerides category.
     *
     * @return triglycerides category label, or {@code null}
     */
    public String getTriglyceridesCategory() {
        return triglyceridesCategory;
    }

    /**
     * Sets triglycerides category.
     *
     * @param triglyceridesCategory category label, or {@code null}
     */
    public void setTriglyceridesCategory(String triglyceridesCategory) {
        this.triglyceridesCategory = triglyceridesCategory;
    }

    /**
     * Returns summary sentence for lipid risk status.
     *
     * @return summary sentence, or {@code null}
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets summary sentence for lipid risk status.
     *
     * @param summary summary sentence, or {@code null}
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Returns an immutable view of detailed lipid alert messages.
     *
     * @return unmodifiable alert list (never {@code null})
     */
    public List<String> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }

    /**
     * Replaces detailed lipid alert messages.
     *
     * @param alerts alert list; {@code null} is treated as an empty list
     */
    public void setAlerts(List<String> alerts) {
        this.alerts = alerts == null ? new ArrayList<>() : new ArrayList<>(alerts);
    }
}
