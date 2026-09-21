package com.ems.session;

import com.ems.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = SessionManager.getInstance();
        sessionManager.logout();
    }

    @AfterEach
    void tearDown() {
        sessionManager.logout();
    }

    @Test
    @DisplayName("Initial session state should be logged out")
    void testInitialState() {
        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getCurrentUser());
        assertNull(sessionManager.getCurrentUserId());
        assertNull(sessionManager.getCurrentRole());
    }

    @Test
    @DisplayName("Login sets session user details correctly")
    void testLoginSuccess() {
        User user = new User();
        user.setUserId(42L);
        user.setOfficialEmail("alice@ems.com");
        user.setRole("ADMIN");
        user.setStatus("ACTIVE");

        sessionManager.login(user);

        assertTrue(sessionManager.isLoggedIn());
        assertEquals(user, sessionManager.getCurrentUser());
        assertEquals(42L, sessionManager.getCurrentUserId());
        assertEquals("ADMIN", sessionManager.getCurrentRole());
    }

    @Test
    @DisplayName("Logout clears session state cleanly")
    void testLogout() {
        User user = new User();
        user.setUserId(10L);
        user.setRole("EMPLOYEE");

        sessionManager.login(user);
        assertTrue(sessionManager.isLoggedIn());

        sessionManager.logout();
        assertFalse(sessionManager.isLoggedIn());
        assertNull(sessionManager.getCurrentUser());
        assertNull(sessionManager.getCurrentUserId());
        assertNull(sessionManager.getCurrentRole());
    }

    @Test
    @DisplayName("Logging in with null user throws IllegalArgumentException")
    void testLoginWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> sessionManager.login(null));
    }
}
