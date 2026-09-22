package com.ems.service;

import com.ems.dto.EmployeeProfileDTO;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;
import com.ems.session.SessionManager;
import com.ems.ui.EmployeeProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeProfileIntegrationTest {

    private final EmployeeService employeeService = new EmployeeService();
    private final DepartmentService departmentService = new DepartmentService();

    @BeforeEach
    void setupAdminSession() {
        SessionManager session = SessionManager.getInstance();
        User adminUser = new User(1L, "admin@ems.com", "hash", "ADMIN", "ACTIVE", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        session.login(adminUser);
    }

    @Test
    @DisplayName("Invalid employee ID throws IllegalArgumentException")
    void testInvalidEmployeeIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeProfile(0));
        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeProfile(-5));
    }

    @Test
    @DisplayName("Non-existent employee ID throws IllegalArgumentException")
    void testNonExistentEmployeeIdThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                employeeService.getEmployeeProfile(999999999L)
        );
        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    @Test
    @DisplayName("Load employee profile from PostgreSQL with basic info and personal info lifecycle")
    void testEmployeeProfileFullLifecycle() {
        List<Department> departments = departmentService.getActiveDepartments();
        assertFalse(departments.isEmpty());
        Department dept = departments.get(0);

        String unique = UUID.randomUUID().toString().substring(0, 8);
        String email = "profile.test." + unique + "@ems.com";
        String fullName = "Profile User " + unique;
        String designation = "Senior Data Analyst";
        LocalDate joiningDate = LocalDate.of(2026, 3, 15);

        Employee created = employeeService.addEmployee(
                fullName,
                email,
                designation,
                dept.getDepartmentId(),
                joiningDate,
                "Pass123456!",
                "Pass123456!"
        );
        assertNotNull(created);
        long employeeId = created.getEmployeeId();

        // 1. Fetch profile with NO personal info
        EmployeeProfileDTO profile = employeeService.getEmployeeProfile(employeeId);
        assertNotNull(profile);
        assertEquals(employeeId, profile.getEmployeeId());
        assertEquals(created.getEmployeeCode(), profile.getEmployeeCode());
        assertEquals(fullName, profile.getFullName());
        assertEquals(designation, profile.getDesignation());
        assertEquals(dept.getName(), profile.getDepartmentName(), "Must return department NAME, not only ID");
        assertEquals(email, profile.getOfficialEmail(), "Must load official email from users table");
        assertEquals(joiningDate, profile.getJoiningDate());
        assertEquals("ACTIVE", profile.getEmployeeStatus());

        // Verify missing personal info does not crash and reports hasPersonalInfo = false
        assertFalse(profile.hasPersonalInfo(), "New employee should not have personal info yet");
        assertNull(profile.getPersonalPhone());
        assertNull(profile.getPersonalEmail());
        assertNull(profile.getAddress());

        // 2. Save personal information
        employeeService.savePersonalInfo(
                employeeId,
                "+1-202-555-0143",
                "personal." + unique + "@gmail.com",
                "742 Evergreen Terrace, Springfield",
                "Jane Doe",
                "+1-202-555-0188",
                "Spouse"
        );

        // 3. Fetch profile AGAIN and verify personal info fields are populated
        EmployeeProfileDTO updatedProfile = employeeService.getEmployeeProfile(employeeId);
        assertNotNull(updatedProfile);
        assertTrue(updatedProfile.hasPersonalInfo());
        assertEquals("+1-202-555-0143", updatedProfile.getPersonalPhone());
        assertEquals("personal." + unique + "@gmail.com", updatedProfile.getPersonalEmail());
        assertEquals("742 Evergreen Terrace, Springfield", updatedProfile.getAddress());
        assertEquals("Jane Doe", updatedProfile.getEmergencyContactName());
        assertEquals("+1-202-555-0188", updatedProfile.getEmergencyContactPhone());
        assertEquals("Spouse", updatedProfile.getEmergencyContactRelation());
    }

    @Test
    @DisplayName("EmployeeProfile screen enforces Admin role access")
    void testEmployeeProfileAdminAccessCheck() {
        SessionManager session = SessionManager.getInstance();

        // Non-admin employee user
        User employeeUser = new User(99L, "emp@ems.com", "hash", "EMPLOYEE", "ACTIVE", null, null, null);
        session.login(employeeUser);

        assertThrows(IllegalStateException.class, () -> new EmployeeProfile(1L));

        // Logged out
        session.logout();
        assertThrows(IllegalStateException.class, () -> new EmployeeProfile(1L));
    }
}
