package com.smartresolve.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBConnection provides a centralized, thread-safe factory for obtaining JDBC
 * database connections to the SmartResolve MySQL database.
 * 
 * Supports environment variables and system properties for configuration,
 * preventing hardcoded production credentials.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Configuration keys
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "smartresolve_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String jdbcUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try {
            // Load MySQL Connector/J driver class
            Class.forName(DRIVER_CLASS);
            initConfiguration();
            LOGGER.info("MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to locate MySQL JDBC Driver (" + DRIVER_CLASS + ") in runtime classpath.", e);
        }
    }

    private DBConnection() {
        // Prevent instantiation of utility class
    }

    /**
     * Initializes connection properties from environment variables, system properties,
     * or fallback local development defaults.
     */
    private static void initConfiguration() {
        String host = getEnvOrDefault("DB_HOST", DEFAULT_HOST);
        String port = getEnvOrDefault("DB_PORT", DEFAULT_PORT);
        String database = getEnvOrDefault("DB_NAME", DEFAULT_DB);

        dbUser = getEnvOrDefault("DB_USER", DEFAULT_USER);
        dbPassword = getEnvOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);

        String customUrl = getEnvOrDefault("DB_URL", null);
        if (customUrl != null && !customUrl.trim().isEmpty()) {
            jdbcUrl = customUrl;
        } else {
            jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, database
            );
        }
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getProperty(key);
        }
        return (value != null && !value.trim().isEmpty()) ? value.trim() : defaultValue;
    }

    /**
     * Obtains a new database connection. Callers must close the returned connection,
     * preferably using try-with-resources.
     * 
     * @return a valid java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to establish MySQL connection to: " + jdbcUrl, e);
            throw e;
        }
    }

    /**
     * Performs a lightweight connectivity check.
     * 
     * @return true if database connection can be established, false otherwise
     */
    public static boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database health-check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely closes one or more AutoCloseable resources without throwing checked exceptions.
     * 
     * @param resources JDBC resources (Connection, Statement, ResultSet) to close
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) {
            return;
        }
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "Error closing resource: " + e.getMessage(), e);
                }
            }
        }
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }
}
