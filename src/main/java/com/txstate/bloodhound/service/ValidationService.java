package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.HealthRecord;
import com.txstate.bloodhound.util.ValidationResult;

/**
 * Applies range and logical validation rules for health records.
 */
public class ValidationService {
    private static final int MIN_SYSTOLIC = 70;
    private static final int MAX_SYSTOLIC = 250;
    private static final int MIN_DIASTOLIC = 40;
    private static final int MAX_DIASTOLIC = 150;
    private static final int MIN_HEART_RATE = 30;
    private static final int MAX_HEART_RATE = 220;
    private static final int MIN_TOTAL_CHOLESTEROL = 80;
    private static final int MAX_TOTAL_CHOLESTEROL = 500;
    private static final int MIN_LDL = 20;
    private static final int MAX_LDL = 350;
    private static final int MIN_HDL = 10;
    private static final int MAX_HDL = 150;
    private static final int MIN_TRIGLYCERIDES = 30;
    private static final int MAX_TRIGLYCERIDES = 1000;

    /**
     * Validates a record and returns all detected issues.
     */
    public ValidationResult validateRecord(HealthRecord record) {
        ValidationResult result = new ValidationResult();
        if (record == null) {
            result.addError("Record is required.");
            return result;
        }

        if (!record.hasAtLeastOneMetric()) {
            result.addError("At least one health metric must be provided.");
        }

        validateRange(record.getSystolic(), MIN_SYSTOLIC, MAX_SYSTOLIC,
                "Systolic blood pressure must be between 70 and 250.", result);
        validateRange(record.getDiastolic(), MIN_DIASTOLIC, MAX_DIASTOLIC,
                "Diastolic blood pressure must be between 40 and 150.", result);
        validateRange(record.getHeartRate(), MIN_HEART_RATE, MAX_HEART_RATE,
                "Heart rate must be between 30 and 220.", result);
        validateRange(record.getTotalCholesterol(), MIN_TOTAL_CHOLESTEROL, MAX_TOTAL_CHOLESTEROL,
                "Total cholesterol must be between 80 and 500.", result);
        validateRange(record.getLdl(), MIN_LDL, MAX_LDL,
                "LDL must be between 20 and 350.", result);
        validateRange(record.getHdl(), MIN_HDL, MAX_HDL,
                "HDL must be between 10 and 150.", result);
        validateRange(record.getTriglycerides(), MIN_TRIGLYCERIDES, MAX_TRIGLYCERIDES,
                "Triglycerides must be between 30 and 1000.", result);

        if (record.getSystolic() != null
                && record.getDiastolic() != null
                && record.getDiastolic() > record.getSystolic()) {
            result.addError("Diastolic pressure cannot exceed systolic pressure.");
        }

        return result;
    }

    private void validateRange(Integer value, int min, int max, String message, ValidationResult result) {
        if (value != null && (value < min || value > max)) {
            result.addError(message);
        }
    }
}
