package com.txstate.bloodhound.util;

/**
 * Centralized database configuration values for JDBC connection management.
 * <p>
 * In production, these values should typically come from environment variables,
 * encrypted configuration, or a secrets manager.
 */
public final class DatabaseConfig {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/bloodhound2";
    public static final String DB_USERNAME = "bloodhound_user";
    public static final String DB_PASSWORD = "change_me";

    private DatabaseConfig() {
        // Utility class; prevent instantiation.
    }
}
