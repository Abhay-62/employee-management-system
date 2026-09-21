package com.ems.service;

import com.ems.dao.UserDAO;
import com.ems.exception.AuthenticationException;
import com.ems.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Service handling user authentication and credential verification.
 */
public class AuthService {

    private static final String GENERIC_AUTH_ERROR = "Invalid email or password.";
    private static final String INACTIVE_ACCOUNT_ERROR = "Account is inactive. Please contact your administrator.";

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "UserDAO must not be null");
    }

    /**
     * Authenticates a user with email and plaintext password.
     *
     * @param email    the user's official email
     * @param password the user's plaintext password
     * @return the authenticated User entity
     * @throws AuthenticationException if credentials are invalid or account is inactive
     * @throws SQLException            if a database access error occurs
     */
    public User authenticate(String email, String password) throws AuthenticationException, SQLException {
        // 1. Validate that email and password are not blank
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException(GENERIC_AUTH_ERROR);
        }

        String sanitizedEmail = email.trim().toLowerCase();

        // 2. Find the user using UserDAO
        Optional<User> userOpt = userDAO.findByOfficialEmail(sanitizedEmail);

        // 3. If no user exists, authentication fails (generic error prevents email enumeration)
        if (userOpt.isEmpty()) {
            throw new AuthenticationException(GENERIC_AUTH_ERROR);
        }

        User user = userOpt.get();

        // 4. Verify entered password against BCrypt password_hash
        boolean passwordMatches = false;
        try {
            if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
                passwordMatches = BCrypt.checkpw(password, user.getPasswordHash());
            }
        } catch (IllegalArgumentException e) {
            // Invalid BCrypt hash structure treated as failed authentication
            passwordMatches = false;
        }

        // 5. If password is incorrect, authentication fails
        if (!passwordMatches) {
            throw new AuthenticationException(GENERIC_AUTH_ERROR);
        }

        // 6. If account status is INACTIVE, authentication fails
        if (!user.isActive()) {
            throw new AuthenticationException(INACTIVE_ACCOUNT_ERROR);
        }

        // 7. If authentication succeeds, update last_login
        userDAO.updateLastLogin(user.getUserId());
        user.setLastLogin(LocalDateTime.now());

        // 8. Return the authenticated User
        return user;
    }
}
