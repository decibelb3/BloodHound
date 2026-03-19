package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Payload returned when a new record is added.
 */
public class AddRecordResponse {
    private final HealthRecord record;
    private final List<String> alerts;

    public AddRecordResponse(HealthRecord record, List<String> alerts) {
        this.record = record;
        this.alerts = alerts == null ? List.of() : new ArrayList<>(alerts);
    }

    public HealthRecord getRecord() {
        return record;
    }

    public List<String> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }
}
