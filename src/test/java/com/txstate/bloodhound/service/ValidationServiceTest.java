package com.txstate.bloodhound.service;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationServiceTest {
    private final ValidationService validationService = new ValidationService();

    @Test
    void validateMeasurementReturnsNoErrorsForValidMeasurement() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, 120, 80, 190, 55, 110, 170.0,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateMeasurementRejectsInvalidSystolic() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, 300, 80, null, null, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Systolic must be between 70 and 250."));
    }

    @Test
    void validateMeasurementRejectsInvalidDiastolic() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, 120, 20, null, null, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Diastolic must be between 40 and 150."));
    }

    @Test
    void validateMeasurementRejectsDiastolicGreaterThanSystolic() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, 100, 110, null, null, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Diastolic cannot exceed systolic."));
    }

    @Test
    void validateMeasurementRejectsInvalidTotalCholesterol() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, null, null, 700, null, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Total cholesterol must be between 80 and 500."));
    }

    @Test
    void validateMeasurementRejectsInvalidHdl() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, null, null, null, 5, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("HDL must be between 10 and 150."));
    }

    @Test
    void validateMeasurementRejectsInvalidLdl() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, null, null, null, null, 10, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("LDL must be between 20 and 350."));
    }

    @Test
    void validateMeasurementRejectsInvalidWeight() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, null, null, null, null, null, 10.0,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Weight must be between 20.0 and 500.0."));
    }

    @Test
    void validateMeasurementRejectsMissingMeasurementDateTime() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, 120, 80, null, null, null, null,
                null);

        List<String> errors = validationService.validateMeasurement(measurement);

        assertTrue(errors.contains("Measurement date and time is required."));
    }

    @Test
    void validateMeasurementRejectsNoMetricsProvided() {
        HealthMeasurement measurement = TestDataFactory.measurement(
                null, 1L, null, null, null, null, null, null,
                LocalDateTime.of(2026, 1, 1, 8, 30));

        List<String> errors = validationService.validateMeasurement(measurement);

        assertFalse(errors.isEmpty());
        assertTrue(errors.contains("At least one health metric must be entered."));
    }
}
