package com.txstate.bloodhound.model;

import java.time.LocalDateTime;

/**
 * Represents a single user-owned health measurement captured at an exact date/time.
 */
public class HealthMeasurement {
    private Long measurementId;
    private Long userId;
    private Integer systolic;
    private Integer diastolic;
    private Integer totalCholesterol;
    private Integer hdl;
    private Integer ldl;
    private Double weight;
    private LocalDateTime measurementDateTime;
    private LocalDateTime createdAt;

    public HealthMeasurement() {
    }

    public HealthMeasurement(Long measurementId,
                             Long userId,
                             Integer systolic,
                             Integer diastolic,
                             Integer totalCholesterol,
                             Integer hdl,
                             Integer ldl,
                             Double weight,
                             LocalDateTime measurementDateTime,
                             LocalDateTime createdAt) {
        this.measurementId = measurementId;
        this.userId = userId;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.totalCholesterol = totalCholesterol;
        this.hdl = hdl;
        this.ldl = ldl;
        this.weight = weight;
        this.measurementDateTime = measurementDateTime;
        this.createdAt = createdAt;
    }

    public Long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(Long measurementId) {
        this.measurementId = measurementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getTotalCholesterol() {
        return totalCholesterol;
    }

    public void setTotalCholesterol(Integer totalCholesterol) {
        this.totalCholesterol = totalCholesterol;
    }

    public Integer getHdl() {
        return hdl;
    }

    public void setHdl(Integer hdl) {
        this.hdl = hdl;
    }

    public Integer getLdl() {
        return ldl;
    }

    public void setLdl(Integer ldl) {
        this.ldl = ldl;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public LocalDateTime getMeasurementDateTime() {
        return measurementDateTime;
    }

    public void setMeasurementDateTime(LocalDateTime measurementDateTime) {
        this.measurementDateTime = measurementDateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "HealthMeasurement{"
                + "measurementId=" + measurementId
                + ", userId=" + userId
                + ", systolic=" + systolic
                + ", diastolic=" + diastolic
                + ", totalCholesterol=" + totalCholesterol
                + ", hdl=" + hdl
                + ", ldl=" + ldl
                + ", weight=" + weight
                + ", measurementDateTime=" + measurementDateTime
                + ", createdAt=" + createdAt
                + '}';
    }
}
