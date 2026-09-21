package com.ems.service;

import com.ems.config.DatabaseConfig;
import com.ems.dao.EmployeeDAO;
import com.ems.dao.UserDAO;
import com.ems.dto.EmployeeProfileDTO;
import com.ems.model.Employee;
import com.ems.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EmployeeService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;

    public EmployeeService() {
        this(new EmployeeDAO(), new UserDAO());
    }

    public EmployeeService(EmployeeDAO employeeDAO, UserDAO userDAO) {
        this.employeeDAO = employeeDAO;
        this.userDAO = userDAO;
    }

    /**
     * Get all employees.
     */
    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load employees.", e);
        }
    }

    /**
     * Get one employee by ID.
     */
    public Optional<Employee> getEmployeeById(long employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be greater than zero.");
        }

        try {
            return employeeDAO.findById(employeeId);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load employee.", e);
        }
    }

    /**
     * Search employees.
     */
    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllEmployees();
        }

        try {
            return employeeDAO.search(keyword);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to search employees.", e);
        }
    }

    /**
     * Atomically creates a user account and linked employee record in a single database transaction.
     *
     * @param fullName the employee's full name
     * @param officialEmail the employee's official company email
     * @param designation the job title/designation
     * @param departmentId the assigned department ID
     * @param joiningDate the joining date
     * @param temporaryPassword the temporary password for initial login
     * @param confirmPassword confirmation of the temporary password
     * @return the created Employee record
     */
    public Employee addEmployee(String fullName, String officialEmail, String designation,
                                Long departmentId, LocalDate joiningDate,
                                String temporaryPassword, String confirmPassword) {
        // 1. Validation checks
        if (fullName == null || fullName.trim().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (officialEmail == null || officialEmail.trim().isBlank()) {
            throw new IllegalArgumentException("Official email is required.");
        }

        String sanitizedEmail = officialEmail.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(sanitizedEmail).matches()) {
            throw new IllegalArgumentException("Please enter a valid official email address (e.g. name@ems.com).");
        }

        if (designation == null || designation.trim().isBlank()) {
            throw new IllegalArgumentException("Designation is required.");
        }

        if (departmentId == null || departmentId <= 0) {
            throw new IllegalArgumentException("Please select a department.");
        }

        if (joiningDate == null) {
            throw new IllegalArgumentException("Joining date is required.");
        }

        if (temporaryPassword == null || temporaryPassword.isBlank()) {
            throw new IllegalArgumentException("Temporary password is required.");
        }

        if (temporaryPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (!temporaryPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match. Please re-enter.");
        }

        // 2. Check duplicate email in users table
        try {
            if (userDAO.existsByOfficialEmail(sanitizedEmail)) {
                throw new IllegalArgumentException("An account with official email '" + sanitizedEmail + "' already exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify email availability: " + e.getMessage(), e);
        }

        // 3. BCrypt hash the temporary password
        String passwordHash = BCrypt.hashpw(temporaryPassword, BCrypt.gensalt(12));

        // 4. Execute atomic transaction (User + Employee)
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // A. Insert User
                User createdUser = userDAO.createUser(conn, sanitizedEmail, passwordHash, "EMPLOYEE", "ACTIVE");

                // B. Generate unique sequential employee code
                String employeeCode = employeeDAO.generateNextEmployeeCode();

                // C. Insert Employee
                Employee employee = new Employee();
                employee.setUserId(createdUser.getUserId());
                employee.setDepartmentId(departmentId);
                employee.setEmployeeCode(employeeCode);
                employee.setFullName(fullName.trim());
                employee.setDesignation(designation.trim());
                employee.setJoiningDate(joiningDate);
                employee.setStatus("ACTIVE");

                Employee createdEmployee = employeeDAO.createEmployee(conn, employee);

                conn.commit();
                return createdEmployee;
            } catch (SQLException e) {
                conn.rollback();
                if ("23505".equals(e.getSQLState())) {
                    if (e.getMessage() != null && e.getMessage().contains("official_email")) {
                        throw new IllegalArgumentException("An account with this official email already exists.");
                    }
                    if (e.getMessage() != null && e.getMessage().contains("employee_code")) {
                        throw new IllegalArgumentException("Employee code conflict. Please try again.");
                    }
                }
                throw new RuntimeException("Failed to create employee record: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while adding employee.", e);
        }
    }

    /**
     * Retrieves the complete employee profile containing basic official details
     * and personal contact info.
     *
     * @param employeeId the employee ID
     * @return the EmployeeProfileDTO
     * @throws IllegalArgumentException if employeeId is invalid or employee not found
     * @throws RuntimeException if a database error occurs
     */
    public EmployeeProfileDTO getEmployeeProfile(long employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID: ID must be greater than zero.");
        }

        try {
            return employeeDAO.findProfileByEmployeeId(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found for ID: " + employeeId));
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load employee profile.", e);
        }
    }

    /**
     * Updates personal information for an employee.
     */
    public void savePersonalInfo(long employeeId, String personalPhone, String personalEmail,
                                 String address, String contactName, String contactPhone,
                                 String contactRelation) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID: ID must be greater than zero.");
        }

        try {
            employeeDAO.upsertPersonalInfo(employeeId, personalPhone, personalEmail, address,
                    contactName, contactPhone, contactRelation);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save personal information.", e);
        }
    }
}
