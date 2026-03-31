package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data access contract for health measurements.
 */
public interface HealthMeasurementDao {
    /**
     * Finds one measurement belonging to a specific user.
     *
     * @param measurementId measurement identifier
     * @param userId owner user identifier
     * @return optional measurement
     * @throws SQLException when query fails
     */
    Optional<HealthMeasurement> findByIdAndUserId(Long measurementId, Long userId) throws SQLException;

    /**
     * Finds all measurements for a user.
     *
     * @param userId owner user identifier
     * @return ordered measurements
     * @throws SQLException when query fails
     */
    List<HealthMeasurement> findByUserId(Long userId) throws SQLException;

    /**
     * Finds user measurements within an inclusive date/time range.
     *
     * @param userId owner user identifier
     * @param startInclusive range start
     * @param endInclusive range end
     * @return matching measurements
     * @throws SQLException when query fails
     */
    List<HealthMeasurement> findByUserIdAndRange(Long userId,
                                                 LocalDateTime startInclusive,
                                                 LocalDateTime endInclusive) throws SQLException;

    /**
     * Persists a new user-owned measurement.
     *
     * @param measurement measurement to persist
     * @return persisted measurement
     * @throws SQLException when persistence fails
     */
    HealthMeasurement create(HealthMeasurement measurement) throws SQLException;

    /**
     * Updates an existing user-owned measurement.
     *
     * @param measurement measurement to update
     * @return updated measurement
     * @throws SQLException when update fails
     */
    HealthMeasurement update(HealthMeasurement measurement) throws SQLException;

    /**
     * Deletes a measurement that belongs to the specified user.
     *
     * @param measurementId measurement identifier
     * @param userId owner user identifier
     * @return true when deletion occurred
     * @throws SQLException when delete fails
     */
    boolean deleteByIdAndUserId(Long measurementId, Long userId) throws SQLException;
}
