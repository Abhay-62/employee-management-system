package com.ems.dao;

import com.ems.config.DatabaseConfig;
import com.ems.dto.EmployeeProfileDTO;
import com.ems.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAO {

    /**
     * Fetch all employees from the database.
     */
    public List<Employee> findAll() throws SQLException {

        String sql = """
                SELECT employee_id,
                       user_id,
                       department_id,
                       employee_code,
                       full_name,
                       designation,
                       joining_date,
                       profile_photo,
                       status,
                       created_at,
                       updated_at
                FROM employees
                ORDER BY employee_id DESC
                """;

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapRow(resultSet));
            }
        }

        return employees;
    }

    /**
     * Find an employee using employee ID.
     */
    public Optional<Employee> findById(long employeeId) throws SQLException {

        String sql = """
                SELECT employee_id,
                       user_id,
                       department_id,
                       employee_code,
                       full_name,
                       designation,
                       joining_date,
                       profile_photo,
                       status,
                       created_at,
                       updated_at
                FROM employees
                WHERE employee_id = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Search employees by employee code, name or designation.
     */
    public List<Employee> search(String keyword) throws SQLException {

        String sql = """
                SELECT employee_id,
                       user_id,
                       department_id,
                       employee_code,
                       full_name,
                       designation,
                       joining_date,
                       profile_photo,
                       status,
                       created_at,
                       updated_at
                FROM employees
                WHERE LOWER(employee_code) LIKE LOWER(?)
                   OR LOWER(full_name) LIKE LOWER(?)
                   OR LOWER(designation) LIKE LOWER(?)
                ORDER BY employee_id DESC
                """;

        List<Employee> employees = new ArrayList<>();

        String searchPattern = "%" + keyword.trim() + "%";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    employees.add(mapRow(resultSet));
                }
            }
        }

        return employees;
    }

    /**
     * Creates a new employee record within a provided connection/transaction.
     *
     * @param connection the active SQL Connection
     * @param employee the Employee object containing employee data
     * @return the created Employee with generated employee_id and timestamps
     * @throws SQLException if a database error occurs
     */
    public Employee createEmployee(Connection connection, Employee employee) throws SQLException {
        String sql = """
                INSERT INTO employees (
                    user_id,
                    department_id,
                    employee_code,
                    full_name,
                    designation,
                    joining_date,
                    profile_photo,
                    status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING employee_id, created_at, updated_at
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, employee.getUserId());
            statement.setLong(2, employee.getDepartmentId());
            statement.setString(3, employee.getEmployeeCode().trim().toUpperCase());
            statement.setString(4, employee.getFullName().trim());
            statement.setString(5, employee.getDesignation().trim());
            statement.setDate(6, java.sql.Date.valueOf(employee.getJoiningDate()));
            statement.setString(7, employee.getProfilePhoto());
            statement.setString(8, employee.getStatus() != null ? employee.getStatus() : "ACTIVE");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    employee.setEmployeeId(resultSet.getLong("employee_id"));
                    if (resultSet.getTimestamp("created_at") != null) {
                        employee.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    }
                    if (resultSet.getTimestamp("updated_at") != null) {
                        employee.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                    }
                    return employee;
                }
            }
        }

        throw new SQLException("Failed to create employee record; no rows returned.");
    }

    /**
     * Checks whether an employee with the specified employee_code exists.
     *
     * @param employeeCode the code to check (e.g. EMP001)
     * @return true if an employee has this code, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean existsByEmployeeCode(String employeeCode) throws SQLException {
        if (employeeCode == null || employeeCode.isBlank()) {
            return false;
        }

        String sql = "SELECT 1 FROM employees WHERE employee_code = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employeeCode.trim().toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Generates the next unique sequential employee code in presentation-MVP format: EMP001, EMP002, etc.
     *
     * @return next unique employee code
     * @throws SQLException if a database error occurs
     */
    public String generateNextEmployeeCode() throws SQLException {
        String sql = "SELECT employee_code FROM employees WHERE employee_code LIKE 'EMP%'";

        int maxNumber = 0;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String code = resultSet.getString("employee_code");
                if (code != null && code.length() > 3) {
                    String numericPart = code.substring(3).trim();
                    try {
                        int val = Integer.parseInt(numericPart);
                        if (val > maxNumber) {
                            maxNumber = val;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        int nextNumber = maxNumber + 1;
        String candidate = String.format("EMP%03d", nextNumber);

        // Ensure no collision exists
        while (existsByEmployeeCode(candidate)) {
            nextNumber++;
            candidate = String.format("EMP%03d", nextNumber);
        }

        return candidate;
    }

    /**
     * Convert one database row into an Employee object.
     */
    private Employee mapRow(ResultSet resultSet) throws SQLException {

        Employee employee = new Employee();

        employee.setEmployeeId(resultSet.getLong("employee_id"));
        employee.setUserId(resultSet.getLong("user_id"));

        long departmentId = resultSet.getLong("department_id");

        if (!resultSet.wasNull()) {
            employee.setDepartmentId(departmentId);
        }

        employee.setEmployeeCode(resultSet.getString("employee_code"));
        employee.setFullName(resultSet.getString("full_name"));
        employee.setDesignation(resultSet.getString("designation"));

        if (resultSet.getDate("joining_date") != null) {
            employee.setJoiningDate(
                    resultSet.getDate("joining_date").toLocalDate()
            );
        }

        employee.setProfilePhoto(resultSet.getString("profile_photo"));
        employee.setStatus(resultSet.getString("status"));

        if (resultSet.getTimestamp("created_at") != null) {
            employee.setCreatedAt(
                    resultSet.getTimestamp("created_at").toLocalDateTime()
            );
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            employee.setUpdatedAt(
                    resultSet.getTimestamp("updated_at").toLocalDateTime()
            );
        }

        return employee;
    }

    private static volatile boolean personalInfoSchemaVerified = false;

    private void ensurePersonalInfoSchema(Connection conn) {
        if (!personalInfoSchemaVerified) {
            synchronized (EmployeeDAO.class) {
                if (!personalInfoSchemaVerified) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.executeUpdate("ALTER TABLE employee_personal_info ADD COLUMN IF NOT EXISTS emergency_contact_relation VARCHAR(100)");
                        stmt.executeUpdate("ALTER TABLE employee_personal_info ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(50)");
                    } catch (SQLException ignored) {
                    }
                    personalInfoSchemaVerified = true;
                }
            }
        }
    }

    /**
     * Loads a complete employee profile joining employees, users, departments,
     * and employee_personal_info in a single query.
     *
     * @param employeeId the employee ID
     * @return Optional containing EmployeeProfileDTO if found, or empty
     * @throws SQLException if a database error occurs
     */
    public Optional<EmployeeProfileDTO> findProfileByEmployeeId(long employeeId) throws SQLException {
        String sql = """
                SELECT 
                    e.employee_id,
                    e.user_id,
                    e.department_id,
                    e.employee_code,
                    e.full_name,
                    e.designation,
                    e.joining_date,
                    e.profile_photo,
                    e.status AS employee_status,
                    u.official_email,
                    u.status AS user_status,
                    d.name AS department_name,
                    d.code AS department_code,
                    epi.personal_phone,
                    epi.personal_email,
                    epi.address,
                    epi.emergency_contact_name,
                    COALESCE(epi.emergency_contact_phone, epi.emergency_contact_number) AS emergency_contact_phone,
                    epi.emergency_contact_relation,
                    epi.updated_at AS personal_info_updated_at
                FROM employees e
                INNER JOIN users u ON e.user_id = u.user_id
                INNER JOIN departments d ON e.department_id = d.department_id
                LEFT JOIN employee_personal_info epi ON e.employee_id = epi.employee_id
                WHERE e.employee_id = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            ensurePersonalInfoSchema(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, employeeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapProfileRow(rs));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private EmployeeProfileDTO mapProfileRow(ResultSet rs) throws SQLException {
        EmployeeProfileDTO p = new EmployeeProfileDTO();
        p.setEmployeeId(rs.getLong("employee_id"));
        p.setUserId(rs.getLong("user_id"));
        p.setDepartmentId(rs.getLong("department_id"));
        p.setEmployeeCode(rs.getString("employee_code"));
        p.setFullName(rs.getString("full_name"));
        p.setDesignation(rs.getString("designation"));
        if (rs.getDate("joining_date") != null) {
            p.setJoiningDate(rs.getDate("joining_date").toLocalDate());
        }
        p.setProfilePhoto(rs.getString("profile_photo"));
        p.setEmployeeStatus(rs.getString("employee_status"));
        p.setOfficialEmail(rs.getString("official_email"));
        p.setUserStatus(rs.getString("user_status"));
        p.setDepartmentName(rs.getString("department_name"));
        p.setDepartmentCode(rs.getString("department_code"));

        p.setPersonalPhone(rs.getString("personal_phone"));
        p.setPersonalEmail(rs.getString("personal_email"));
        p.setAddress(rs.getString("address"));
        p.setEmergencyContactName(rs.getString("emergency_contact_name"));
        p.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        p.setEmergencyContactRelation(rs.getString("emergency_contact_relation"));
        if (rs.getTimestamp("personal_info_updated_at") != null) {
            p.setPersonalInfoUpdatedAt(rs.getTimestamp("personal_info_updated_at").toLocalDateTime());
        }
        return p;
    }

    /**
     * Upserts an employee's personal info record.
     */
    public void upsertPersonalInfo(long employeeId, String personalPhone, String personalEmail,
                                   String address, String contactName, String contactPhone,
                                   String contactRelation) throws SQLException {
        String sql = """
                INSERT INTO employee_personal_info (
                    employee_id, personal_phone, personal_email, address,
                    emergency_contact_name, emergency_contact_phone, emergency_contact_number,
                    emergency_contact_relation, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (employee_id) DO UPDATE SET
                    personal_phone = EXCLUDED.personal_phone,
                    personal_email = EXCLUDED.personal_email,
                    address = EXCLUDED.address,
                    emergency_contact_name = EXCLUDED.emergency_contact_name,
                    emergency_contact_phone = EXCLUDED.emergency_contact_phone,
                    emergency_contact_number = EXCLUDED.emergency_contact_number,
                    emergency_contact_relation = EXCLUDED.emergency_contact_relation,
                    updated_at = CURRENT_TIMESTAMP
                """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            ensurePersonalInfoSchema(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, employeeId);
                stmt.setString(2, personalPhone);
                stmt.setString(3, personalEmail);
                stmt.setString(4, address);
                stmt.setString(5, contactName);
                stmt.setString(6, contactPhone);
                stmt.setString(7, contactPhone);
                stmt.setString(8, contactRelation);
                stmt.executeUpdate();
            }
        }
    }
}
