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
