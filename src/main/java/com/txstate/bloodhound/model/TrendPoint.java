package com.txstate.bloodhound.model;

import java.time.LocalDateTime;

/**
 * Represents a single chart point used for trend visualization.
 */
public class TrendPoint {
    private LocalDateTime timestamp;
    private Double value;

    public TrendPoint() {
    }

    public TrendPoint(LocalDateTime timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
