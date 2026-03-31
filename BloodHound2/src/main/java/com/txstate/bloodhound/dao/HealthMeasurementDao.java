package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data access contract for user-scoped health measurements.
 */
public interface HealthMeasurementDao {
    /**
     * Persists a new health measurement.
     *
     * @param measurement measurement to persist
     * @return persisted measurement with generated id if available
     * @throws SQLException when persistence fails
     */
    HealthMeasurement save(HealthMeasurement measurement) throws SQLException;

    /**
     * Finds all measurements for the specified user.
     *
     * @param userId owner user identifier
     * @return measurements for the user
     * @throws SQLException when query fails
     */
    List<HealthMeasurement> findByUserId(Long userId) throws SQLException;

    /**
     * Finds all measurements for a user ordered by measurement date ascending.
     *
     * @param userId owner user identifier
     * @return ordered measurement history
     * @throws SQLException when query fails
     */
    List<HealthMeasurement> findByUserIdOrderedByDate(Long userId) throws SQLException;

    /**
     * Finds all measurements for a user within an inclusive date range, ordered by measurement date ascending.
     *
     * @param userId owner user identifier
     * @param start range start (inclusive)
     * @param end range end (inclusive)
     * @return matching measurements
     * @throws SQLException when query fails
     */
    List<HealthMeasurement> findByUserIdAndDateRange(Long userId,
                                                     LocalDateTime start,
                                                     LocalDateTime end) throws SQLException;

    /**
     * Finds the latest measurement for a user.
     *
     * @param userId owner user identifier
     * @return optional latest measurement
     * @throws SQLException when query fails
     */
    Optional<HealthMeasurement> findLatestByUserId(Long userId) throws SQLException;

    /**
     * Deletes one measurement for the owning user.
     *
     * @param measurementId measurement identifier
     * @param userId owner user identifier
     * @throws SQLException when delete fails
     */
    void deleteByIdAndUserId(Long measurementId, Long userId) throws SQLException;

    /**
     * Updates an existing user-scoped measurement.
     *
     * @param measurement measurement payload
     * @return updated measurement payload
     * @throws SQLException when update fails
     */
    HealthMeasurement update(HealthMeasurement measurement) throws SQLException;
}
