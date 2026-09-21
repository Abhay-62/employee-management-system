package com.ems.dao;

import com.ems.config.DatabaseConfig;
import com.ems.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 * Handles database operations against the 'users' table using PreparedStatement and try-with-resources.
 */
public class UserDAO {

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT user_id, official_email, password_hash, role, status, last_login, created_at, updated_at " +
            "FROM users WHERE official_email = ?";

    private static final String UPDATE_LAST_LOGIN_SQL =
            "UPDATE users SET last_login = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

    /**
     * Finds a user record by their official email address.
     *
     * @param email the official email address to search
     * @return an Optional containing the User if found, or empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    public Optional<User> findByOfficialEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {

            stmt.setString(1, email.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates the last_login timestamp and updated_at timestamp for a given user ID.
     *
     * @param userId the ID of the user to update
     * @throws SQLException if a database access error occurs
     */
    public void updateLastLogin(long userId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_LAST_LOGIN_SQL)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new user record within a provided connection/transaction.
     *
     * @param conn the active SQL Connection
     * @param officialEmail the unique official email
     * @param passwordHash the BCrypt password hash
     * @param role the user role (e.g. EMPLOYEE)
     * @param status the user status (e.g. ACTIVE)
     * @return the created User with generated user_id and timestamps
     * @throws SQLException if an error occurs during insert
     */
    public User createUser(Connection conn, String officialEmail, String passwordHash, String role, String status) throws SQLException {
        String sql = "INSERT INTO users (official_email, password_hash, role, status) " +
                     "VALUES (?, ?, ?, ?) " +
                     "RETURNING user_id, official_email, password_hash, role, status, last_login, created_at, updated_at";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, officialEmail.trim().toLowerCase());
            stmt.setString(2, passwordHash);
            stmt.setString(3, role);
            stmt.setString(4, status != null ? status : "ACTIVE");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        throw new SQLException("Failed to create user record; no rows returned.");
    }

    /**
     * Checks if a user already exists with the given official email.
     *
     * @param email the email to check
     * @return true if an account exists with this email, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean existsByOfficialEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM users WHERE official_email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");

        return new User(
                rs.getLong("user_id"),
                rs.getString("official_email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("status"),
                lastLoginTs != null ? lastLoginTs.toLocalDateTime() : null,
                createdAtTs != null ? createdAtTs.toLocalDateTime() : null,
                updatedAtTs != null ? updatedAtTs.toLocalDateTime() : null
        );
    }
}
