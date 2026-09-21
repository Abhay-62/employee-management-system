package com.ems;

import com.ems.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Temporary development test utility for verifying database connectivity.
 * Isolated from the future application architecture.
 *
 * NOTE: Never print, log, or expose credentials or passwords.
 */
public class DatabaseConnectionTest {

    public static void main(String[] args) {
        System.out.println("[DEV TEST] Verifying database connectivity...");

        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[DEV TEST] Connection status: SUCCESS (Successfully connected to database)");
            } else {
                System.err.println("[DEV TEST] Connection status: FAILED (Connection was null or closed)");
            }
        } catch (IllegalStateException e) {
            // Raised when configuration is missing or placeholder values are detected
            System.err.println("[DEV TEST] Configuration status: PENDING");
            System.err.println("[DEV TEST] Details: " + e.getMessage());
        } catch (SQLException e) {
            // Connection or authentication failure without leaking credentials
            System.err.println("[DEV TEST] Connection status: FAILED");
            System.err.println("[DEV TEST] Reason: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[DEV TEST] Unexpected error occurred: " + e.getMessage());
        }
    }
}
