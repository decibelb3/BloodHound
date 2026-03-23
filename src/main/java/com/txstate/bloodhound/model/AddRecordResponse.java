package com.txstate.bloodhound.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Response payload returned when a new record is successfully added.
 */
public class AddRecordResponse {
    /** Persisted record including generated identifiers and classifications. */
    private final HealthRecord record;
    /** Risk alerts generated while classifying the saved record. */
    private final List<String> alerts;

    /**
     * Creates a response for an add-record operation.
     *
     * @param record persisted record payload
     * @param alerts risk alert messages; {@code null} is treated as an empty list
     */
    public AddRecordResponse(HealthRecord record, List<String> alerts) {
        this.record = record;
        this.alerts = alerts == null ? List.of() : new ArrayList<>(alerts);
    }

    /**
     * Returns the persisted record payload.
     *
     * @return saved record
     */
    public HealthRecord getRecord() {
        return record;
    }

    /**
     * Returns risk alert messages generated for the record.
     *
     * @return immutable list of alert messages
     */
    public List<String> getAlerts() {
        return Collections.unmodifiableList(alerts);
    }
}
