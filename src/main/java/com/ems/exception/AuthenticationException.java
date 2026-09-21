package com.ems.exception;

/**
 * Exception thrown when an authentication attempt fails due to invalid credentials,
 * inactive account status, or missing required fields.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
