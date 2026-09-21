package com.ems.service;

import com.ems.dao.DashboardDAO;
import com.ems.dto.AdminDashboardStats;
import com.ems.dto.EmployeeDashboardData;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Service providing aggregated metrics and presentation data for application dashboards.
 * Protects UI components from raw SQL exceptions and manages graceful defaults.
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this(new DashboardDAO());
    }

    public DashboardService(DashboardDAO dashboardDAO) {
        this.dashboardDAO = Objects.requireNonNull(dashboardDAO, "DashboardDAO must not be null");
    }

    /**
     * Loads live metrics for the Admin Dashboard.
     *
     * @return AdminDashboardStats containing live counts and recent employees
     */
    public AdminDashboardStats getAdminDashboardStats() {
        try {
            return dashboardDAO.getAdminDashboardStats();
        } catch (SQLException e) {
            System.err.println("Notice: Failed to load admin dashboard metrics: " + e.getMessage());
            return AdminDashboardStats.empty();
        }
    }

    /**
     * Loads profile and document metrics for the logged-in employee.
     *
     * @param userId the user ID of the authenticated employee
     * @return EmployeeDashboardData containing profile and metrics
     */
    public EmployeeDashboardData getEmployeeDashboardData(long userId) {
        try {
            return dashboardDAO.getEmployeeDashboardData(userId);
        } catch (SQLException e) {
            System.err.println("Notice: Failed to load employee dashboard data: " + e.getMessage());
            return EmployeeDashboardData.pendingProfile("ACTIVE");
        }
    }
}
