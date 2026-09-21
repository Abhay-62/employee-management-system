package com.ems.dao;

import com.ems.config.DatabaseConfig;
import com.ems.dto.AdminDashboardStats;
import com.ems.dto.EmployeeDashboardData;
import com.ems.dto.RecentEmployeeDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for dashboard metrics and overview queries.
 * Employs PreparedStatement, try-with-resources, and safe result mapping.
 */
public class DashboardDAO {

    private static final String COUNT_EMPLOYEES_SQL =
            "SELECT " +
            "    COUNT(*) AS total_count, " +
            "    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS active_count, " +
            "    COUNT(CASE WHEN status = 'INACTIVE' THEN 1 END) AS inactive_count " +
            "FROM employees";

    private static final String COUNT_DEPARTMENTS_SQL =
            "SELECT COUNT(*) AS dept_count FROM departments";

    private static final String RECENT_EMPLOYEES_SQL =
            "SELECT e.employee_id, e.employee_code, e.full_name, " +
            "       COALESCE(d.name, 'Unassigned') AS dept_name, " +
            "       e.designation, e.joining_date, e.status " +
            "FROM employees e " +
            "LEFT JOIN departments d ON e.department_id = d.department_id " +
            "ORDER BY e.created_at DESC " +
            "LIMIT 5";

    private static final String FIND_EMPLOYEE_BY_USER_ID_SQL =
            "SELECT e.employee_id, e.employee_code, e.full_name, " +
            "       COALESCE(d.name, 'Unassigned') AS dept_name, " +
            "       e.designation, e.status " +
            "FROM employees e " +
            "LEFT JOIN departments d ON e.department_id = d.department_id " +
            "WHERE e.user_id = ?";

    private static final String COUNT_EMPLOYEE_DOCUMENTS_SQL =
            "SELECT " +
            "    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) AS pending_docs, " +
            "    COUNT(CASE WHEN status = 'APPROVED' THEN 1 END) AS approved_docs " +
            "FROM documents " +
            "WHERE employee_id = ?";

    /**
     * Retrieves aggregated counts and recent employee listings for the Admin Dashboard.
     *
     * @return AdminDashboardStats populated with live database counts
     * @throws SQLException if a database query fails
     */
    public AdminDashboardStats getAdminDashboardStats() throws SQLException {
        long totalEmp = 0;
        long activeEmp = 0;
        long inactiveEmp = 0;
        long totalDept = 0;
        List<RecentEmployeeDTO> recentList = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection()) {
            // 1. Query employee count metrics
            try (PreparedStatement stmt = conn.prepareStatement(COUNT_EMPLOYEES_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalEmp = rs.getLong("total_count");
                    activeEmp = rs.getLong("active_count");
                    inactiveEmp = rs.getLong("inactive_count");
                }
            }

            // 2. Query department count
            try (PreparedStatement stmt = conn.prepareStatement(COUNT_DEPARTMENTS_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalDept = rs.getLong("dept_count");
                }
            }

            // 3. Query recent employees
            try (PreparedStatement stmt = conn.prepareStatement(RECENT_EMPLOYEES_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date joinDate = rs.getDate("joining_date");
                    LocalDate localJoinDate = joinDate != null ? joinDate.toLocalDate() : null;

                    recentList.add(new RecentEmployeeDTO(
                            rs.getLong("employee_id"),
                            rs.getString("employee_code"),
                            rs.getString("full_name"),
                            rs.getString("dept_name"),
                            rs.getString("designation"),
                            localJoinDate,
                            rs.getString("status")
                    ));
                }
            }
        }

        return new AdminDashboardStats(totalEmp, activeEmp, inactiveEmp, totalDept, recentList);
    }

    /**
     * Retrieves employee profile details and document status metrics for a specific user.
     *
     * @param userId the user ID of the authenticated employee
     * @return EmployeeDashboardData populated with profile and document counts
     * @throws SQLException if a database query fails
     */
    public EmployeeDashboardData getEmployeeDashboardData(long userId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Long employeeId = null;
            String employeeCode = null;
            String fullName = null;
            String deptName = null;
            String designation = null;
            String status = null;

            // 1. Find employee profile for this user ID
            try (PreparedStatement stmt = conn.prepareStatement(FIND_EMPLOYEE_BY_USER_ID_SQL)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        employeeId = rs.getLong("employee_id");
                        employeeCode = rs.getString("employee_code");
                        fullName = rs.getString("full_name");
                        deptName = rs.getString("dept_name");
                        designation = rs.getString("designation");
                        status = rs.getString("status");
                    }
                }
            }

            // If no profile exists yet for this user account, return pending profile placeholder
            if (employeeId == null) {
                return EmployeeDashboardData.pendingProfile("ACTIVE");
            }

            // 2. Query document verification metrics for this employee
            long pendingDocs = 0;
            long approvedDocs = 0;
            try (PreparedStatement stmt = conn.prepareStatement(COUNT_EMPLOYEE_DOCUMENTS_SQL)) {
                stmt.setLong(1, employeeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        pendingDocs = rs.getLong("pending_docs");
                        approvedDocs = rs.getLong("approved_docs");
                    }
                }
            }

            return new EmployeeDashboardData(
                    true, employeeId, employeeCode, fullName, deptName, designation,
                    status != null ? status : "ACTIVE", pendingDocs, approvedDocs, 0
            );
        }
    }
}
