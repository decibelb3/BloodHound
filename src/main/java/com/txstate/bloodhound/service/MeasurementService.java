package com.txstate.bloodhound.service;

import com.txstate.bloodhound.dao.HealthMeasurementDao;
import com.txstate.bloodhound.model.HealthMeasurement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Coordinates user-scoped CRUD operations for health measurements.
 */
public class MeasurementService {
    private final HealthMeasurementDao healthMeasurementDao;

    public MeasurementService(HealthMeasurementDao healthMeasurementDao) {
        this.healthMeasurementDao = healthMeasurementDao;
    }

    /**
     * Creates a new measurement for a user.
     *
     * @param userId owner user id
     * @param measurement measurement data
     * @return stored measurement
     * @throws SQLException when persistence fails
     */
    public HealthMeasurement createMeasurement(Long userId, HealthMeasurement measurement) throws SQLException {
        // TODO: Validate ownership and fields before insert.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Lists all measurements for a user.
     *
     * @param userId owner user id
     * @return ordered measurements
     * @throws SQLException when query fails
     */
    public List<HealthMeasurement> getMeasurements(Long userId) throws SQLException {
        // TODO: Add sorting and pagination behavior if needed.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Lists measurements for a user in a date/time range.
     *
     * @param userId owner user id
     * @param startInclusive start timestamp
     * @param endInclusive end timestamp
     * @return matching measurements
     * @throws SQLException when query fails
     */
    public List<HealthMeasurement> getMeasurementsInRange(Long userId,
                                                          LocalDateTime startInclusive,
                                                          LocalDateTime endInclusive) throws SQLException {
        // TODO: Validate range before DAO call.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Updates a user-owned measurement.
     *
     * @param userId owner user id
     * @param measurement measurement payload
     * @return updated measurement
     * @throws SQLException when update fails
     */
    public HealthMeasurement updateMeasurement(Long userId, HealthMeasurement measurement) throws SQLException {
        // TODO: Verify ownership then delegate to DAO.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Deletes a user-owned measurement.
     *
     * @param userId owner user id
     * @param measurementId measurement id
     * @return true when deleted
     * @throws SQLException when delete fails
     */
    public boolean deleteMeasurement(Long userId, Long measurementId) throws SQLException {
        // TODO: Verify ownership and delete.
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public HealthMeasurementDao getHealthMeasurementDao() {
        return healthMeasurementDao;
    }
}
