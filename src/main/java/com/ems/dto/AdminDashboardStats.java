package com.ems.dto;

import java.util.Collections;
import java.util.List;

/**
 * Aggregated statistics and recent employee records for the Admin Dashboard.
 */
public class AdminDashboardStats {

    private final long totalEmployees;
    private final long activeEmployees;
    private final long inactiveEmployees;
    private final long totalDepartments;
    private final List<RecentEmployeeDTO> recentEmployees;

    public AdminDashboardStats(long totalEmployees, long activeEmployees, long inactiveEmployees,
                               long totalDepartments, List<RecentEmployeeDTO> recentEmployees) {
        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.inactiveEmployees = inactiveEmployees;
        this.totalDepartments = totalDepartments;
        this.recentEmployees = recentEmployees != null ? recentEmployees : Collections.emptyList();
    }

    public static AdminDashboardStats empty() {
        return new AdminDashboardStats(0, 0, 0, 0, Collections.emptyList());
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public long getInactiveEmployees() {
        return inactiveEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public List<RecentEmployeeDTO> getRecentEmployees() {
        return recentEmployees;
    }
}
