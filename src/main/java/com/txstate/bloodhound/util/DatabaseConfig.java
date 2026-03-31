package com.txstate.bloodhound.util;

/**
 * Centralized database configuration values for JDBC connection management.
 * <p>
 * Values are read from environment variables first, with safe local defaults:
 * <ul>
 *     <li>{@code BLOODHOUND_DB_URL}</li>
 *     <li>{@code BLOODHOUND_DB_USERNAME}</li>
 *     <li>{@code BLOODHOUND_DB_PASSWORD}</li>
 * </ul>
 * This keeps local development simple while allowing secure runtime overrides.
 */
public final class DatabaseConfig {
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/bloodhound2";
    private static final String DEFAULT_DB_USERNAME = "bloodhound_user";
    private static final String DEFAULT_DB_PASSWORD = "change_me";

    public static final String DB_URL = envOrDefault("BLOODHOUND_DB_URL", DEFAULT_DB_URL);
    public static final String DB_USERNAME = envOrDefault("BLOODHOUND_DB_USERNAME", DEFAULT_DB_USERNAME);
    public static final String DB_PASSWORD = envOrDefault("BLOODHOUND_DB_PASSWORD", DEFAULT_DB_PASSWORD);

    private DatabaseConfig() {
        // Utility class; prevent instantiation.
    }

    private static String envOrDefault(String variableName, String defaultValue) {
        String value = System.getenv(variableName);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}
