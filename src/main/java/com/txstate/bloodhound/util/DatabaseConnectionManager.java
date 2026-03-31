package com.txstate.bloodhound.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralizes JDBC connection settings and connection creation for MySQL.
 */
public class DatabaseConnectionManager {
    private String jdbcUrl;
    private String username;
    private String password;

    public DatabaseConnectionManager() {
    }

    public DatabaseConnectionManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Opens a JDBC connection using current configuration.
     *
     * @return active JDBC connection
     * @throws SQLException when connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
