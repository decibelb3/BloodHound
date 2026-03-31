package com.txstate.bloodhound.model;

import java.time.LocalDateTime;

/**
 * Represents a single metric value captured at a point in time for chart rendering.
 */
public class MetricPoint {
    private LocalDateTime timestamp;
    private Double value;

    public MetricPoint() {
    }

    public MetricPoint(LocalDateTime timestamp, Double value) {
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

    @Override
    public String toString() {
        return "MetricPoint{"
                + "timestamp=" + timestamp
                + ", value=" + value
                + '}';
    }
}
