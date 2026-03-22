package com.txstate.bloodhound.ui;

import com.txstate.bloodhound.model.HealthRecord;

import java.time.LocalDate;

/**
 * Shared lightweight helpers for desktop UI record formatting/parsing.
 */
public final class UiRecordHelper {
    private UiRecordHelper() {
        // Utility class.
    }

    public static LocalDate parseDateField(String fieldName, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required (format: YYYY-MM-DD).");
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must use format YYYY-MM-DD.");
        }
    }

    public static Integer parseOptionalInteger(String fieldName, String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be numeric.");
        }
    }

    public static String nullIfBlank(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    public static String buildTagSummary(HealthRecord record) {
        return "timeOfDay=" + valueOrBlank(record.getTimeOfDay())
                + ", medTiming=" + valueOrBlank(record.getMedTiming())
                + ", activityTiming=" + valueOrBlank(record.getActivityTiming());
    }

    public static String formatDouble(Double value) {
        return value == null ? "N/A" : String.format("%.1f", value);
    }

    public static String valueOrBlank(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
