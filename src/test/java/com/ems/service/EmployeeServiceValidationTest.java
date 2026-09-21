package com.ems.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceValidationTest {

    private final EmployeeService employeeService = new EmployeeService();

    @Test
    @DisplayName("Null or blank full name throws IllegalArgumentException")
    void testMissingFullName() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee(null, "test@ems.com", "Dev", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Full name is required.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("   ", "test@ems.com", "Dev", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Full name is required.", ex2.getMessage());
    }

    @Test
    @DisplayName("Null or blank official email throws IllegalArgumentException")
    void testMissingOfficialEmail() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", null, "Dev", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Official email is required.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "  ", "Dev", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Official email is required.", ex2.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@ems.com", "test.com", "user@site"})
    @DisplayName("Invalid official email format throws IllegalArgumentException")
    void testInvalidEmailFormat(String invalidEmail) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", invalidEmail, "Dev", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertTrue(ex.getMessage().contains("valid official email address"));
    }

    @Test
    @DisplayName("Null or blank designation throws IllegalArgumentException")
    void testMissingDesignation() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "  ", 1L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Designation is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Null or non-positive department ID throws IllegalArgumentException")
    void testMissingDepartment() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", null, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Please select a department.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 0L, LocalDate.now(), "Secret123", "Secret123")
        );
        assertEquals("Please select a department.", ex2.getMessage());
    }

    @Test
    @DisplayName("Null joining date throws IllegalArgumentException")
    void testMissingJoiningDate() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 1L, null, "Secret123", "Secret123")
        );
        assertEquals("Joining date is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Null, blank or short password throws IllegalArgumentException")
    void testInvalidPassword() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 1L, LocalDate.now(), null, null)
        );
        assertEquals("Temporary password is required.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 1L, LocalDate.now(), "  ", "  ")
        );
        assertEquals("Temporary password is required.", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 1L, LocalDate.now(), "12345", "12345")
        );
        assertEquals("Password must be at least 6 characters long.", ex3.getMessage());
    }

    @Test
    @DisplayName("Mismatched confirm password throws IllegalArgumentException")
    void testMismatchedPassword() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("John Doe", "john@ems.com", "Dev", 1L, LocalDate.now(), "Secret123", "Different123")
        );
        assertEquals("Passwords do not match. Please re-enter.", ex.getMessage());
    }
}
