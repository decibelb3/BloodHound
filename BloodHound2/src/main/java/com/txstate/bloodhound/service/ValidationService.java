package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.HealthMeasurement;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates measurement payloads against business rules and plausible metric ranges.
 */
public class ValidationService {
    private static final int MIN_SYSTOLIC = 70;
    private static final int MAX_SYSTOLIC = 250;
    private static final int MIN_DIASTOLIC = 40;
    private static final int MAX_DIASTOLIC = 150;
    private static final int MIN_TOTAL_CHOLESTEROL = 80;
    private static final int MAX_TOTAL_CHOLESTEROL = 500;
    private static final int MIN_HDL = 10;
    private static final int MAX_HDL = 150;
    private static final int MIN_LDL = 20;
    private static final int MAX_LDL = 350;
    private static final double MIN_WEIGHT = 20.0;
    private static final double MAX_WEIGHT = 500.0;

    /**
     * Validates a health measurement payload.
     *
     * @param measurement payload to validate
     * @return validation error list (empty when valid)
     */
    public List<String> validateMeasurement(HealthMeasurement measurement) {
        List<String> errors = new ArrayList<>();
        if (measurement == null) {
            errors.add("Measurement is required.");
            return errors;
        }

        if (!hasAnyMetric(measurement)) {
            errors.add("At least one health metric must be entered.");
        }

        if (measurement.getMeasurementDateTime() == null) {
            errors.add("Measurement date and time is required.");
        }

        validateIntegerRange(measurement.getSystolic(), MIN_SYSTOLIC, MAX_SYSTOLIC,
                "Systolic must be between 70 and 250.", errors);
        validateIntegerRange(measurement.getDiastolic(), MIN_DIASTOLIC, MAX_DIASTOLIC,
                "Diastolic must be between 40 and 150.", errors);
        validateIntegerRange(measurement.getTotalCholesterol(), MIN_TOTAL_CHOLESTEROL, MAX_TOTAL_CHOLESTEROL,
                "Total cholesterol must be between 80 and 500.", errors);
        validateIntegerRange(measurement.getHdl(), MIN_HDL, MAX_HDL,
                "HDL must be between 10 and 150.", errors);
        validateIntegerRange(measurement.getLdl(), MIN_LDL, MAX_LDL,
                "LDL must be between 20 and 350.", errors);
        validateDoubleRange(measurement.getWeight(), MIN_WEIGHT, MAX_WEIGHT,
                "Weight must be between 20.0 and 500.0.", errors);

        if (measurement.getSystolic() != null
                && measurement.getDiastolic() != null
                && measurement.getDiastolic() > measurement.getSystolic()) {
            errors.add("Diastolic cannot exceed systolic.");
        }

        return errors;
    }

    private boolean hasAnyMetric(HealthMeasurement measurement) {
        return measurement.getSystolic() != null
                || measurement.getDiastolic() != null
                || measurement.getTotalCholesterol() != null
                || measurement.getHdl() != null
                || measurement.getLdl() != null
                || measurement.getWeight() != null;
    }

    private void validateIntegerRange(Integer value, int min, int max, String message, List<String> errors) {
        if (value != null && (value < min || value > max)) {
            errors.add(message);
        }
    }

    private void validateDoubleRange(Double value, double min, double max, String message, List<String> errors) {
        if (value != null && (value < min || value > max)) {
            errors.add(message);
        }
    }
}
