package com.txstate.bloodhound.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages JDBC connection creation for MySQL.
 * <p>
 * This class is intentionally limited to connection concerns only.
 */
public class DatabaseConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());

    private final String jdbcUrl;
    private final String username;
    private final String password;

    /**
     * Creates a connection manager using values from {@link DatabaseConfig}.
     */
    public DatabaseConnectionManager() {
        this(DatabaseConfig.DB_URL, DatabaseConfig.DB_USERNAME, DatabaseConfig.DB_PASSWORD);
    }

    /**
     * Creates a connection manager using supplied JDBC configuration.
     *
     * @param jdbcUrl JDBC URL
     * @param username database username
     * @param password database password
     */
    public DatabaseConnectionManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "jdbcUrl must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Opens a JDBC connection using current configuration.
     *
     * @return active JDBC connection
     * @throws SQLException when connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Unable to establish MySQL connection: {0}", exception.getMessage());
            throw exception;
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
