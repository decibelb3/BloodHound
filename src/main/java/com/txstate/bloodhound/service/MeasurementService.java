package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.OperationResult;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates validation and user-scoped CRUD operations for health measurements.
 */
public class MeasurementService {
    private final HealthMeasurementDao healthMeasurementDao;
    private final ValidationService validationService;

    public MeasurementService(HealthMeasurementDao healthMeasurementDao) {
        this(healthMeasurementDao, new ValidationService());
    }

    public MeasurementService(HealthMeasurementDao healthMeasurementDao, ValidationService validationService) {
        this.healthMeasurementDao = healthMeasurementDao;
        this.validationService = validationService;
    }

    /**
     * Determines whether a measurement id belongs to a specific user.
     *
     * @param userId owner user id
     * @param measurementId target measurement id
     * @return true when the measurement exists for the user
     * @throws SQLException when lookup fails
     */
    public boolean isMeasurementOwnedByUser(Long userId, Long measurementId) throws SQLException {
        return healthMeasurementDao.findByUserId(userId).stream()
                .anyMatch(measurement -> measurementId.equals(measurement.getMeasurementId()));
    }

    /**
     * Validates and stores a new measurement for a user.
     *
     * @param userId owner user id
     * @param measurement measurement payload
     * @return operation result with stored measurement
     */
    public OperationResult<HealthMeasurement> addMeasurement(Long userId, HealthMeasurement measurement) {
        if (!isValidUserId(userId)) {
            return OperationResult.failure("Validation failed.", List.of("userId is required."));
        }
        if (measurement == null) {
            return OperationResult.failure("Validation failed.", List.of("Measurement is required."));
        }
        if (measurement.getUserId() != null && !userId.equals(measurement.getUserId())) {
            return OperationResult.failure("Validation failed.",
                    List.of("Measurements must belong to the authenticated user."));
        }

        measurement.setUserId(userId);
        List<String> validationErrors = validationService.validateMeasurement(measurement);
        if (!validationErrors.isEmpty()) {
            return OperationResult.failure("Validation failed.", validationErrors);
        }

        if (measurement.getCreatedAt() == null) {
            measurement.setCreatedAt(LocalDateTime.now());
        }

        try {
            HealthMeasurement stored = healthMeasurementDao.save(measurement);
            return OperationResult.success("Measurement saved successfully.", stored);
        } catch (SQLException exception) {
            return OperationResult.failure("Unable to save measurement.", List.of(exception.getMessage()));
        }
    }

    /**
     * Returns full measurement history for a user ordered by measurement date.
     *
     * @param userId owner user id
     * @return ordered measurements
     */
    public List<HealthMeasurement> getMeasurementHistory(Long userId) {
        if (!isValidUserId(userId)) {
            return List.of();
        }
        try {
            return healthMeasurementDao.findByUserIdOrderedByDate(userId);
        } catch (SQLException exception) {
            return List.of();
        }
    }

    /**
     * Retrieves measurements in an inclusive date/time range for a user.
     *
     * @param userId owner user id
     * @param start range start
     * @param end range end
     * @return matching measurements
     */
    public OperationResult<List<HealthMeasurement>> getMeasurementsByDateRange(Long userId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end) {
        List<String> errors = new ArrayList<>();
        if (!isValidUserId(userId)) {
            errors.add("userId is required.");
        }
        if (start == null) {
            errors.add("Start date/time is required.");
        }
        if (end == null) {
            errors.add("End date/time is required.");
        }
        if (start != null && end != null && end.isBefore(start)) {
            errors.add("End date/time cannot be before start date/time.");
        }
        if (!errors.isEmpty()) {
            return OperationResult.failure("Validation failed.", errors);
        }

        try {
            List<HealthMeasurement> measurements = healthMeasurementDao.findByUserIdAndDateRange(userId, start, end);
            return OperationResult.success("Measurement range loaded.", measurements);
        } catch (SQLException exception) {
            return OperationResult.failure("Unable to load measurement range.", List.of(exception.getMessage()));
        }
    }

    /**
     * Validates and updates a user-owned measurement.
     *
     * @param userId owner user id
     * @param measurement measurement payload
     * @return operation result with updated measurement
     */
    public OperationResult<HealthMeasurement> updateMeasurement(Long userId, HealthMeasurement measurement) {
        if (!isValidUserId(userId)) {
            return OperationResult.failure("Validation failed.", List.of("userId is required."));
        }
        if (measurement == null) {
            return OperationResult.failure("Validation failed.", List.of("Measurement is required."));
        }
        if (measurement.getMeasurementId() == null) {
            return OperationResult.failure("Validation failed.", List.of("measurementId is required."));
        }

        measurement.setUserId(userId);
        List<String> validationErrors = validationService.validateMeasurement(measurement);
        if (!validationErrors.isEmpty()) {
            return OperationResult.failure("Validation failed.", validationErrors);
        }

        try {
            if (!isMeasurementOwnedByUser(userId, measurement.getMeasurementId())) {
                return OperationResult.failure("Update failed.",
                        List.of("Measurement not found for the authenticated user."));
            }

            HealthMeasurement updated = healthMeasurementDao.update(measurement);
            return OperationResult.success("Measurement updated successfully.", updated);
        } catch (SQLException exception) {
            return OperationResult.failure("Unable to update measurement.", List.of(exception.getMessage()));
        }
    }

    /**
     * Deletes a measurement only if it belongs to the authenticated user.
     *
     * @param userId owner user id
     * @param measurementId measurement id
     * @return operation result for deletion
     */
    public OperationResult<Void> deleteMeasurement(Long userId, Long measurementId) {
        if (!isValidUserId(userId)) {
            return OperationResult.failure("Validation failed.", List.of("userId is required."));
        }
        if (measurementId == null) {
            return OperationResult.failure("Validation failed.", List.of("measurementId is required."));
        }

        try {
            if (!isMeasurementOwnedByUser(userId, measurementId)) {
                return OperationResult.failure("Delete failed.",
                        List.of("Measurement not found for the authenticated user."));
            }

            healthMeasurementDao.deleteByIdAndUserId(measurementId, userId);
            return OperationResult.success("Measurement deleted successfully.", null);
        } catch (SQLException exception) {
            return OperationResult.failure("Unable to delete measurement.", List.of(exception.getMessage()));
        }
    }

    public HealthMeasurementDao getHealthMeasurementDao() {
        return healthMeasurementDao;
    }

    public ValidationService getValidationService() {
        return validationService;
    }

    private boolean isValidUserId(Long userId) {
        return userId != null && userId > 0;
    }
}
