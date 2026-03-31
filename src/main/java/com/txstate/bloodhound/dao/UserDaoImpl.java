package com.txstate.bloodhound.dao;

import com.txstate.bloodhound.model.User;
import com.txstate.bloodhound.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JDBC implementation of {@link UserDao} for MySQL user persistence.
 */
public class UserDaoImpl implements UserDao {
    private static final String SELECT_BASE =
            "SELECT id, username, email, password_hash, created_at FROM users ";

    private static final String SELECT_BY_ID = SELECT_BASE + "WHERE id = ?";
    private static final String SELECT_BY_USERNAME = SELECT_BASE + "WHERE username = ?";
    private static final String SELECT_BY_EMAIL = SELECT_BASE + "WHERE email = ?";
    private static final String EXISTS_BY_USERNAME = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
    private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
    private static final String INSERT_USER =
            "INSERT INTO users (username, email, password_hash, created_at) VALUES (?, ?, ?, ?)";

    private final DatabaseConnectionManager connectionManager;

    public UserDaoImpl(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return findSingle(SELECT_BY_ID, userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findSingle(SELECT_BY_USERNAME, username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findSingle(SELECT_BY_EMAIL, email);
    }

    @Override
    public User createUser(User user) {
        LocalDateTime createdAt = user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setTimestamp(4, Timestamp.valueOf(createdAt));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getLong(1));
                }
            }
            user.setCreatedAt(createdAt);
            return user;
        } catch (SQLException exception) {
            throw mapException("Failed to create user", exception);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists(EXISTS_BY_USERNAME, username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists(EXISTS_BY_EMAIL, email);
    }

    private Optional<User> findSingle(String sql, Object value) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindValue(statement, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapUser(resultSet));
            }
        } catch (SQLException exception) {
            throw mapException("Failed to query user record", exception);
        }
    }

    private boolean exists(String sql, String value) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw mapException("Failed to execute user existence query", exception);
        }
    }

    private void bindValue(PreparedStatement statement, Object value) throws SQLException {
        if (value instanceof Long longValue) {
            statement.setLong(1, longValue);
        } else if (value instanceof String stringValue) {
            statement.setString(1, stringValue);
        } else {
            throw new IllegalArgumentException("Unsupported bind value type: " + value);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));

        Timestamp createdAtTimestamp = resultSet.getTimestamp("created_at");
        user.setCreatedAt(createdAtTimestamp != null ? createdAtTimestamp.toLocalDateTime() : null);
        return user;
    }

    private RuntimeException mapException(String message, SQLException exception) {
        return new IllegalStateException(message + ": " + exception.getMessage(), exception);
    }

    public DatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
