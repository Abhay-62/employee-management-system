package com.ems.session;

import com.ems.model.User;
import java.util.Objects;

/**
 * Manages the in-memory session for the currently authenticated user in the desktop application.
 * Sessions are strictly kept in memory and are not persisted to disk.
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private volatile User currentUser;

    private SessionManager() {
    }

    /**
     * Returns the singleton instance of SessionManager.
     *
     * @return the SessionManager instance
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Establishes a session for the authenticated user.
     *
     * @param user the authenticated User entity
     * @throws IllegalArgumentException if user is null
     */
    public synchronized void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when establishing a session.");
        }
        this.currentUser = user;
    }

    /**
     * Terminates the current session and clears the authenticated user state.
     */
    public synchronized void logout() {
        this.currentUser = null;
    }

    /**
     * Checks whether an authenticated user is currently active in the session.
     *
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Returns the currently authenticated User, or null if no user is logged in.
     *
     * @return the current User or null
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Returns the user ID of the currently authenticated user, or null if no user is logged in.
     *
     * @return the current user ID or null
     */
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * Returns the role of the currently authenticated user, or null if no user is logged in.
     *
     * @return the current role string (e.g. "ADMIN", "EMPLOYEE") or null
     */
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}
