package com.txstate.bloodhound.model;

import java.time.LocalDateTime;

/**
 * Represents a single user-owned health measurement captured at an exact date/time.
 */
public class HealthMeasurement {
    private Long id;
    private Long userId;
    private Integer systolicBloodPressure;
    private Integer diastolicBloodPressure;
    private Integer totalCholesterol;
    private Integer hdl;
    private Integer ldl;
    private Double weight;
    private LocalDateTime measuredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HealthMeasurement() {
    }

    public HealthMeasurement(Long id,
                             Long userId,
                             Integer systolicBloodPressure,
                             Integer diastolicBloodPressure,
                             Integer totalCholesterol,
                             Integer hdl,
                             Integer ldl,
                             Double weight,
                             LocalDateTime measuredAt,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.systolicBloodPressure = systolicBloodPressure;
        this.diastolicBloodPressure = diastolicBloodPressure;
        this.totalCholesterol = totalCholesterol;
        this.hdl = hdl;
        this.ldl = ldl;
        this.weight = weight;
        this.measuredAt = measuredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public void setSystolicBloodPressure(Integer systolicBloodPressure) {
        this.systolicBloodPressure = systolicBloodPressure;
    }

    public Integer getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public void setDiastolicBloodPressure(Integer diastolicBloodPressure) {
        this.diastolicBloodPressure = diastolicBloodPressure;
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

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
