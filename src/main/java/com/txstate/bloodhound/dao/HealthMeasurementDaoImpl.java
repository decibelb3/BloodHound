package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * JDBC implementation of {@link HealthMeasurementDao} for MySQL persistence.
 * <p>
 * All read and write operations are scoped to the owning user.
 */
public class HealthMeasurementDaoImpl implements HealthMeasurementDao {
    private static final String BASE_SELECT = """
            SELECT id, user_id, systolic, diastolic, total_cholesterol, hdl, ldl, weight, measured_at, created_at
            FROM health_measurements
            """;

    private static final String INSERT_SQL = """
            INSERT INTO health_measurements
            (user_id, systolic, diastolic, total_cholesterol, hdl, ldl, weight, measured_at, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE health_measurements
            SET systolic = ?, diastolic = ?, total_cholesterol = ?, hdl = ?, ldl = ?, weight = ?, measured_at = ?
            WHERE id = ? AND user_id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM health_measurements
            WHERE id = ? AND user_id = ?
            """;

    private final DatabaseConnectionManager connectionManager;

    /**
     * Creates a DAO with connection manager dependency.
     *
     * @param connectionManager connection provider
     */
    public HealthMeasurementDaoImpl(DatabaseConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(connectionManager, "connectionManager must not be null");
    }

    @Override
    public HealthMeasurement save(HealthMeasurement measurement) throws SQLException {
        validateMeasurementForWrite(measurement);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindMeasurementForInsert(statement, measurement);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    measurement.setMeasurementId(generatedKeys.getLong(1));
                }
            }
            return measurement;
        }
    }

    @Override
    public List<HealthMeasurement> findByUserId(Long userId) throws SQLException {
        final String sql = BASE_SELECT + " WHERE user_id = ?";
        return findListByUserAndQuery(userId, sql, null, null);
    }

    @Override
    public List<HealthMeasurement> findByUserIdOrderedByDate(Long userId) throws SQLException {
        final String sql = BASE_SELECT + " WHERE user_id = ? ORDER BY measured_at DESC";
        return findListByUserAndQuery(userId, sql, null, null);
    }

    @Override
    public List<HealthMeasurement> findByUserIdAndDateRange(Long userId,
                                                            LocalDateTime start,
                                                            LocalDateTime end) throws SQLException {
        validateUserId(userId);
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");

        final String sql = BASE_SELECT
                + " WHERE user_id = ? AND measured_at >= ? AND measured_at <= ? ORDER BY measured_at ASC";
        return findListByUserAndQuery(userId, sql, start, end);
    }

    @Override
    public Optional<HealthMeasurement> findLatestByUserId(Long userId) throws SQLException {
        validateUserId(userId);
        final String sql = BASE_SELECT + " WHERE user_id = ? ORDER BY measured_at DESC, id DESC LIMIT 1";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapMeasurement(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteByIdAndUserId(Long measurementId, Long userId) throws SQLException {
        validateMeasurementId(measurementId);
        validateUserId(userId);

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, measurementId);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    @Override
    public HealthMeasurement update(HealthMeasurement measurement) throws SQLException {
        validateMeasurementForWrite(measurement);
        validateMeasurementId(measurement.getMeasurementId());

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            bindMeasurementForUpdate(statement, measurement);
            statement.executeUpdate();
            return measurement;
        }
    }

    /**
     * Returns the active connection manager used by this DAO.
     *
     * @return connection manager
     */
    public DatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private List<HealthMeasurement> findListByUserAndQuery(Long userId,
                                                           String sql,
                                                           LocalDateTime start,
                                                           LocalDateTime end) throws SQLException {
        validateUserId(userId);
        List<HealthMeasurement> measurements = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            if (start != null && end != null) {
                statement.setTimestamp(2, Timestamp.valueOf(start));
                statement.setTimestamp(3, Timestamp.valueOf(end));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    measurements.add(mapMeasurement(resultSet));
                }
            }
        }
        return measurements;
    }

    private void bindMeasurementForInsert(PreparedStatement statement, HealthMeasurement measurement) throws SQLException {
        statement.setLong(1, measurement.getUserId());
        statement.setObject(2, measurement.getSystolic());
        statement.setObject(3, measurement.getDiastolic());
        statement.setObject(4, measurement.getTotalCholesterol());
        statement.setObject(5, measurement.getHdl());
        statement.setObject(6, measurement.getLdl());
        statement.setObject(7, measurement.getWeight());
        statement.setTimestamp(8, Timestamp.valueOf(measurement.getMeasurementDateTime()));
        statement.setTimestamp(9, measurement.getCreatedAt() == null
                ? Timestamp.valueOf(LocalDateTime.now())
                : Timestamp.valueOf(measurement.getCreatedAt()));
    }

    private void bindMeasurementForUpdate(PreparedStatement statement, HealthMeasurement measurement) throws SQLException {
        statement.setObject(1, measurement.getSystolic());
        statement.setObject(2, measurement.getDiastolic());
        statement.setObject(3, measurement.getTotalCholesterol());
        statement.setObject(4, measurement.getHdl());
        statement.setObject(5, measurement.getLdl());
        statement.setObject(6, measurement.getWeight());
        statement.setTimestamp(7, Timestamp.valueOf(measurement.getMeasurementDateTime()));
        statement.setLong(8, measurement.getMeasurementId());
        statement.setLong(9, measurement.getUserId());
    }

    private HealthMeasurement mapMeasurement(ResultSet resultSet) throws SQLException {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setMeasurementId(resultSet.getLong("id"));
        measurement.setUserId(resultSet.getLong("user_id"));
        measurement.setSystolic(getNullableInteger(resultSet, "systolic"));
        measurement.setDiastolic(getNullableInteger(resultSet, "diastolic"));
        measurement.setTotalCholesterol(getNullableInteger(resultSet, "total_cholesterol"));
        measurement.setHdl(getNullableInteger(resultSet, "hdl"));
        measurement.setLdl(getNullableInteger(resultSet, "ldl"));
        measurement.setWeight(getNullableDouble(resultSet, "weight"));
        measurement.setMeasurementDateTime(toLocalDateTime(resultSet.getTimestamp("measured_at")));
        measurement.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return measurement;
    }

    private Integer getNullableInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        int value = resultSet.getInt(columnLabel);
        return resultSet.wasNull() ? null : value;
    }

    private Double getNullableDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        double value = resultSet.getDouble(columnLabel);
        return resultSet.wasNull() ? null : value;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private void validateMeasurementForWrite(HealthMeasurement measurement) {
        Objects.requireNonNull(measurement, "measurement must not be null");
        validateUserId(measurement.getUserId());
        Objects.requireNonNull(measurement.getMeasurementDateTime(), "measurementDateTime must not be null");
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId must be a positive value.");
        }
    }

    private void validateMeasurementId(Long measurementId) {
        if (measurementId == null || measurementId <= 0) {
            throw new IllegalArgumentException("measurementId must be a positive value.");
        }
    }
}
