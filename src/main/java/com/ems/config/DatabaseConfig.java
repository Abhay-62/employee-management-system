package com.ems.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database configuration and provides JDBC connections for the application.
 * Loads connection properties securely from config.properties or environment variables.
 * Ensures credentials and passwords are never logged, printed, or exposed.
 */
public final class DatabaseConfig {

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private static boolean initialized = false;

    // Utility class; prevent instantiation
    private DatabaseConfig() {
    }

    /**
     * Initializes and loads database configuration if not already initialized.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        Properties props = new Properties();
        boolean loaded = false;

        // 1. Try loading from classpath (e.g. src/main/resources/config.properties)
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (in != null) {
                props.load(in);
                loaded = true;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + CONFIG_FILE_NAME + " from classpath", e);
        }

        // 2. Fallback: Try loading from working directory
        if (!loaded) {
            File externalFile = new File(CONFIG_FILE_NAME);
            if (externalFile.exists() && externalFile.isFile()) {
                try (InputStream in = new FileInputStream(externalFile)) {
                    props.load(in);
                    loaded = true;
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to read " + CONFIG_FILE_NAME + " from working directory", e);
                }
            }
        }

        // Resolve connection settings with environment variable fallbacks
        dbUrl = resolveValue(props, "db.url", "DB_URL", "DATABASE_URL");
        dbUsername = resolveValue(props, "db.username", "DB_USERNAME", "DB_USER");
        dbPassword = resolveValue(props, "db.password", "DB_PASSWORD");

        // Validate presence of required connection parameters
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException("Database URL is missing. Set 'db.url' in " + CONFIG_FILE_NAME + " or set DB_URL environment variable.");
        }
        if (dbUsername == null || dbUsername.isBlank()) {
            throw new IllegalStateException("Database username is missing. Set 'db.username' in " + CONFIG_FILE_NAME + " or set DB_USERNAME environment variable.");
        }
        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException("Database password is missing. Set 'db.password' in " + CONFIG_FILE_NAME + " or set DB_PASSWORD environment variable.");
        }

        // Validate that placeholders have been replaced
        if (isPlaceholder(dbUrl) || isPlaceholder(dbUsername) || isPlaceholder(dbPassword)) {
            throw new IllegalStateException("Database configuration contains unresolved placeholders. Please configure actual credentials in " + CONFIG_FILE_NAME + ".");
        }

        // Verify PostgreSQL driver availability
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC Driver class not found in classpath.", e);
        }

        initialized = true;
    }

    private static String resolveValue(Properties props, String propKey, String... envKeys) {
        // 1. Check environment variables or system properties first (supports CLI/container overrides)
        for (String envKey : envKeys) {
            String envVal = System.getenv(envKey);
            if (envVal != null && !envVal.isBlank()) {
                return envVal.trim();
            }
            String sysVal = System.getProperty(envKey);
            if (sysVal != null && !sysVal.isBlank()) {
                return sysVal.trim();
            }
        }
        // 2. Fall back to properties file
        String val = props.getProperty(propKey);
        if (val != null && !val.isBlank()) {
            return val.trim();
        }
        return null;
    }

    private static boolean isPlaceholder(String value) {
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase();
        return lower.contains("[your-project-ref]")
                || lower.contains("your_supabase_db_password")
                || lower.contains("<supabase_host>")
                || lower.contains("<supabase_user>")
                || lower.contains("<supabase_password>")
                || lower.contains("your_database_password");
    }

    /**
     * Returns the database URL.
     */
    public static synchronized String getUrl() {
        if (!initialized) {
            initialize();
        }
        return dbUrl;
    }

    /**
     * Returns the database username.
     */
    public static synchronized String getUsername() {
        if (!initialized) {
            initialize();
        }
        return dbUsername;
    }

    /**
     * Returns the database password safely without printing or logging.
     */
    public static synchronized String getPassword() {
        if (!initialized) {
            initialize();
        }
        return dbPassword;
    }

    /**
     * Creates and returns a new JDBC Connection to the configured PostgreSQL/Supabase database.
     *
     * @return an open {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            // Re-throw with clean message that never exposes sensitive credentials
            throw new SQLException("Failed to establish database connection to: " + dbUrl + ". Reason: " + e.getMessage(), e);
        }
    }
}
