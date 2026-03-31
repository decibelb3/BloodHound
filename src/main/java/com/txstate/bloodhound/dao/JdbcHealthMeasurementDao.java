package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.DatabaseConnectionManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-backed implementation placeholder for health measurement persistence.
 */
public class JdbcHealthMeasurementDao implements HealthMeasurementDao {
    private final DatabaseConnectionManager connectionManager;

    public JdbcHealthMeasurementDao(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<HealthMeasurement> findByIdAndUserId(Long measurementId, Long userId) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<HealthMeasurement> findByUserId(Long userId) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public List<HealthMeasurement> findByUserIdAndRange(Long userId,
                                                        LocalDateTime startInclusive,
                                                        LocalDateTime endInclusive) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HealthMeasurement create(HealthMeasurement measurement) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public HealthMeasurement update(HealthMeasurement measurement) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean deleteByIdAndUserId(Long measurementId, Long userId) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public DatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
