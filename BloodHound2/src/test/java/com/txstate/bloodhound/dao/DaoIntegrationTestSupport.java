package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.HealthMeasurement;
import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Shared H2 setup utilities for DAO integration tests.
 */
final class DaoIntegrationTestSupport {

    private DaoIntegrationTestSupport() {
    }

    static DatabaseConnectionManager createConnectionManager() {
        String url = "jdbc:h2:mem:bloodhound_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
        return new DatabaseConnectionManager(url, "sa", "");
    }

    static void createSchema(DatabaseConnectionManager manager) throws SQLException {
        try (Connection connection = manager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(100) NOT NULL UNIQUE,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password_hash VARCHAR(512) NOT NULL,
                      created_at TIMESTAMP NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS health_measurements (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      systolic INT NULL,
                      diastolic INT NULL,
                      total_cholesterol INT NULL,
                      hdl INT NULL,
                      ldl INT NULL,
                      weight DOUBLE NULL,
                      measured_at TIMESTAMP NOT NULL,
                      created_at TIMESTAMP NOT NULL,
                      CONSTRAINT fk_health_user
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    )
                    """);
        }
    }

    static void truncateAll(DatabaseConnectionManager manager) throws SQLException {
        try (Connection connection = manager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM health_measurements");
            statement.execute("DELETE FROM users");
        }
    }

    static User seedUser(Connection connection, String username, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                User user = new User();
                user.setUserId(keys.getLong(1));
                user.setUsername(username);
                user.setEmail(email);
                user.setPasswordHash(passwordHash);
                return user;
            }
        }
    }

    static HealthMeasurement seedMeasurement(Connection connection,
                                             long userId,
                                             Integer systolic,
                                             Integer diastolic,
                                             Integer totalCholesterol,
                                             Integer hdl,
                                             Integer ldl,
                                             Double weight,
                                             LocalDateTime measuredAt) throws SQLException {
        String sql = """
                INSERT INTO health_measurements
                (user_id, systolic, diastolic, total_cholesterol, hdl, ldl, weight, measured_at, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setObject(2, systolic);
            statement.setObject(3, diastolic);
            statement.setObject(4, totalCholesterol);
            statement.setObject(5, hdl);
            statement.setObject(6, ldl);
            statement.setObject(7, weight);
            statement.setTimestamp(8, Timestamp.valueOf(measuredAt));
            statement.setTimestamp(9, Timestamp.valueOf(measuredAt.plusMinutes(1)));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                HealthMeasurement measurement = new HealthMeasurement();
                measurement.setMeasurementId(keys.getLong(1));
                measurement.setUserId(userId);
                measurement.setSystolic(systolic);
                measurement.setDiastolic(diastolic);
                measurement.setTotalCholesterol(totalCholesterol);
                measurement.setHdl(hdl);
                measurement.setLdl(ldl);
                measurement.setWeight(weight);
                measurement.setMeasurementDateTime(measuredAt);
                measurement.setCreatedAt(measuredAt.plusMinutes(1));
                return measurement;
            }
        }
    }
}
