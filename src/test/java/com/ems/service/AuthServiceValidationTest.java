package com.ems.service;

import com.ems.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceValidationTest {

    private final AuthService authService = new AuthService();

    @ParameterizedTest
    @CsvSource({
            "'', 'password123'",
            "'   ', 'password123'",
            "'user@ems.com', ''",
            "'user@ems.com', '   '"
    })
    @DisplayName("Blank or null credentials throw AuthenticationException immediately")
    void testBlankCredentialsReject(String email, String password) {
        assertThrows(AuthenticationException.class, () -> authService.authenticate(email, password));
    }

    @Test
    @DisplayName("Null credentials throw AuthenticationException immediately")
    void testNullCredentialsReject() {
        assertThrows(AuthenticationException.class, () -> authService.authenticate(null, "password"));
        assertThrows(AuthenticationException.class, () -> authService.authenticate("user@ems.com", null));
        assertThrows(AuthenticationException.class, () -> authService.authenticate(null, null));
    }

    @Test
    @DisplayName("BCrypt successfully verifies valid password and rejects invalid password")
    void testBCryptPasswordVerificationLogic() {
        String rawPassword = "CorrectHorseBatteryStaple#2026";
        String wrongPassword = "WrongPassword#2026";

        String salt = BCrypt.gensalt(10);
        String hash = BCrypt.hashpw(rawPassword, salt);

        assertNotNull(hash);
        assertTrue(BCrypt.checkpw(rawPassword, hash), "BCrypt must verify matching password");
        assertFalse(BCrypt.checkpw(wrongPassword, hash), "BCrypt must reject incorrect password");
    }
}
