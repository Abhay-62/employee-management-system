package com.ems.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("User getters and setters operate correctly")
    void testUserProperties() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "admin@ems.com", "$2a$12$hashedSecret", "ADMIN", "ACTIVE", now, now, now);

        assertEquals(1L, user.getUserId());
        assertEquals("admin@ems.com", user.getOfficialEmail());
        assertEquals("$2a$12$hashedSecret", user.getPasswordHash());
        assertEquals("ADMIN", user.getRole());
        assertEquals("ACTIVE", user.getStatus());
        assertEquals(now, user.getLastLogin());
        assertTrue(user.isActive());
        assertTrue(user.isAdmin());
    }

    @Test
    @DisplayName("User toString must never expose plaintext or hashed password")
    void testToStringDoesNotLeakPasswordHash() {
        String secretHash = "$2a$12$superSecretHashValue1234567890";
        User user = new User("employee@ems.com", secretHash, "EMPLOYEE", "ACTIVE");

        String toStringOutput = user.toString();
        assertFalse(toStringOutput.contains(secretHash), "toString() must NOT contain the password hash");
        assertTrue(toStringOutput.contains("[PROTECTED]"), "toString() should mark password hash as protected");
    }

    @Test
    @DisplayName("User status and role checks operate correctly")
    void testStatusAndRoleChecks() {
        User inactiveUser = new User("emp@ems.com", "hash", "EMPLOYEE", "INACTIVE");
        assertFalse(inactiveUser.isActive());
        assertFalse(inactiveUser.isAdmin());

        User activeAdmin = new User("admin@ems.com", "hash", "ADMIN", "ACTIVE");
        assertTrue(activeAdmin.isActive());
        assertTrue(activeAdmin.isAdmin());
    }
}
