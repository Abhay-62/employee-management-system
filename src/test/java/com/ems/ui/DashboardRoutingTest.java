package com.ems.ui;

import com.ems.model.User;
import com.ems.session.SessionManager;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class DashboardRoutingTest {

    @BeforeEach
    void checkGuiEnvironment() {
        // Skip GUI tests if environment is purely headless
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
        SessionManager.getInstance().logout();
    }

    @Test
    @DisplayName("AdminDashboard initializes with correct title and session user")
    void testAdminDashboardInit() {
        User admin = new User();
        admin.setUserId(1L);
        admin.setOfficialEmail("admin@ems.com");
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");

        SessionManager.getInstance().login(admin);

        AdminDashboard dashboard = new AdminDashboard(admin);
        assertNotNull(dashboard);
        assertTrue(dashboard.getTitle().contains("Admin Dashboard"));
        assertEquals("ADMIN", SessionManager.getInstance().getCurrentRole());

        dashboard.dispose();
    }

    @Test
    @DisplayName("EmployeeDashboard initializes with correct title and session user")
    void testEmployeeDashboardInit() {
        User employee = new User();
        employee.setUserId(2L);
        employee.setOfficialEmail("employee@ems.com");
        employee.setRole("EMPLOYEE");
        employee.setStatus("ACTIVE");

        SessionManager.getInstance().login(employee);

        EmployeeDashboard dashboard = new EmployeeDashboard(employee);
        assertNotNull(dashboard);
        assertTrue(dashboard.getTitle().contains("Employee Dashboard"));
        assertEquals("EMPLOYEE", SessionManager.getInstance().getCurrentRole());

        dashboard.dispose();
    }

    @Test
    @DisplayName("LoginScreen initializes successfully with components")
    void testLoginScreenInit() {
        LoginScreen loginScreen = new LoginScreen();
        assertNotNull(loginScreen);
        assertTrue(loginScreen.getTitle().contains("Login"));
        loginScreen.dispose();
    }
}
