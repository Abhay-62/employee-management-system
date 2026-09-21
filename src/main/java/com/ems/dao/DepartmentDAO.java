package com.ems.dao;

import com.ems.config.DatabaseConfig;
import com.ems.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Department entities.
 */
public class DepartmentDAO {

    private static final String FIND_ALL_ACTIVE_SQL =
            "SELECT department_id, name, code, description, status, created_at, updated_at " +
            "FROM departments " +
            "WHERE status = 'ACTIVE' " +
            "ORDER BY name ASC";

    private static final String FIND_BY_ID_SQL =
            "SELECT department_id, name, code, description, status, created_at, updated_at " +
            "FROM departments " +
            "WHERE department_id = ?";

    private static final String INSERT_SQL =
            "INSERT INTO departments (name, code, description, status) " +
            "VALUES (?, ?, ?, ?) " +
            "RETURNING department_id, name, code, description, status, created_at, updated_at";

    /**
     * Loads all active departments from the database.
     *
     * @return List of active Department entities
     * @throws SQLException if a database access error occurs
     */
    public List<Department> findAllActive() throws SQLException {
        seedDefaultDepartmentsIfEmpty();
        List<Department> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_ACTIVE_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Finds a department by its unique ID.
     *
     * @param departmentId the department ID
     * @return Optional containing the Department if found
     * @throws SQLException if a database access error occurs
     */
    public Optional<Department> findById(long departmentId) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setLong(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a new department record.
     *
     * @param department the department to insert
     * @return the created Department with generated ID and timestamps
     * @throws SQLException if a database access error occurs
     */
    public Department create(Department department) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getCode());
            stmt.setString(3, department.getDescription());
            stmt.setString(4, department.getStatus() != null ? department.getStatus() : "ACTIVE");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new SQLException("Creating department failed, no rows returned.");
    }

    /**
     * Seeds essential standard departments if the departments table is completely empty.
     *
     * @throws SQLException if a database access error occurs
     */
    public void seedDefaultDepartmentsIfEmpty() throws SQLException {
        String countSql = "SELECT COUNT(*) FROM departments";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String seedSql = """
                INSERT INTO departments (name, code, description, status) VALUES
                ('Engineering', 'ENG', 'Software and Infrastructure Engineering', 'ACTIVE'),
                ('Human Resources', 'HR', 'People Operations and Talent Acquisition', 'ACTIVE'),
                ('Finance', 'FIN', 'Financial Planning, Accounting and Payroll', 'ACTIVE'),
                ('Marketing', 'MKT', 'Brand Strategy and Communications', 'ACTIVE'),
                ('Operations', 'OPS', 'Business Operations and Facilities', 'ACTIVE')
                ON CONFLICT (code) DO NOTHING
                """;
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(seedSql);
        }
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");

        return new Department(
                rs.getLong("department_id"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getString("description"),
                rs.getString("status"),
                createdTs != null ? createdTs.toLocalDateTime() : null,
                updatedTs != null ? updatedTs.toLocalDateTime() : null
        );
    }
}
