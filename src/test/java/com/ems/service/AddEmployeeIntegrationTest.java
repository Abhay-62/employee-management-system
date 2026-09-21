package com.ems.service;

import com.ems.config.DatabaseConfig;
import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.dao.UserDAO;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddEmployeeIntegrationTest {

    private final EmployeeService employeeService = new EmployeeService();
    private final DepartmentService departmentService = new DepartmentService();
    private final AuthService authService = new AuthService();
    private final UserDAO userDAO = new UserDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Test
    @DisplayName("End-to-End verification of Add Employee workflow against PostgreSQL")
    void testAddEmployeeFullWorkflow() throws Exception {
        // Step D: Departments are loaded from database
        List<Department> departments = departmentService.getActiveDepartments();
        assertNotNull(departments, "Departments list must not be null");
        assertFalse(departments.isEmpty(), "Active departments must be loaded from DB");
        Department selectedDept = departments.get(0);
        long deptId = selectedDept.getDepartmentId();

        // Step E: Create new employee with unique email
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String testEmail = "test.emp." + uniqueSuffix + "@ems.com";
        String testPassword = "TempPassword123!";
        String testFullName = "Test Employee " + uniqueSuffix;
        String testDesignation = "Software Engineer";
        LocalDate joiningDate = LocalDate.now();

        Employee createdEmployee = employeeService.addEmployee(
                testFullName,
                testEmail,
                testDesignation,
                deptId,
                joiningDate,
                testPassword,
                testPassword
        );

        assertNotNull(createdEmployee);
        assertTrue(createdEmployee.getEmployeeId() > 0, "Employee ID must be generated");
        assertNotNull(createdEmployee.getEmployeeCode(), "Employee code must be generated");
        assertTrue(createdEmployee.getEmployeeCode().startsWith("EMP"), "Code format must start with EMP");

        // Step F: Employee appears in table / search
        List<Employee> searchResults = employeeService.searchEmployees(testFullName);
        assertFalse(searchResults.isEmpty(), "New employee must be searchable and returned in employee list");

        // Step G, H, I, J: Verify row in users, linked row in employees, role = EMPLOYEE, BCrypt hash
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement userStmt = conn.prepareStatement("SELECT * FROM users WHERE official_email = ?");
             PreparedStatement empStmt = conn.prepareStatement("SELECT * FROM employees WHERE employee_id = ?")) {

            userStmt.setString(1, testEmail.toLowerCase());
            try (ResultSet rs = userStmt.executeQuery()) {
                assertTrue(rs.next(), "Step G: Row must exist in users table");
                long userId = rs.getLong("user_id");
                assertEquals("EMPLOYEE", rs.getString("role"), "Step I: Role must be EMPLOYEE");
                assertEquals("ACTIVE", rs.getString("status"), "Status must be ACTIVE");

                String passwordHash = rs.getString("password_hash");
                assertNotNull(passwordHash);
                assertNotEquals(testPassword, passwordHash, "Step J: Password must never be stored as plaintext");
                assertTrue(passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$"), "Step J: Password must be a valid BCrypt hash");
                assertTrue(BCrypt.checkpw(testPassword, passwordHash), "Step J: BCrypt check must verify with plaintext password");

                // Check Step H: linked row in employees
                empStmt.setLong(1, createdEmployee.getEmployeeId());
                try (ResultSet empRs = empStmt.executeQuery()) {
                    assertTrue(empRs.next(), "Step H: Linked row must exist in employees table");
                    assertEquals(userId, empRs.getLong("user_id"), "Step H: Employee must link to created user_id");
                    assertEquals(deptId, empRs.getLong("department_id"));
                    assertEquals(createdEmployee.getEmployeeCode(), empRs.getString("employee_code"));
                    assertEquals(testFullName, empRs.getString("full_name"));
                    assertEquals(testDesignation, empRs.getString("designation"));
                    assertEquals("ACTIVE", empRs.getString("status"));
                }
            }
        }

        // Step K: Try duplicate email and confirm it is rejected
        IllegalArgumentException dupEx = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee(
                        "Duplicate User",
                        testEmail,
                        "Analyst",
                        deptId,
                        joiningDate,
                        "AnotherPass123",
                        "AnotherPass123"
                )
        );
        assertTrue(dupEx.getMessage().contains("already exists"), "Duplicate email must be rejected with clear message");

        // Step L: Try mismatched passwords and confirm validation
        IllegalArgumentException mismatchEx = assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee(
                        "Mismatch User",
                        "mismatch@ems.com",
                        "Analyst",
                        deptId,
                        joiningDate,
                        "Password123",
                        "Password999"
                )
        );
        assertTrue(mismatchEx.getMessage().contains("match"), "Mismatched passwords must be rejected");

        // Step M: Try missing required fields and confirm validation
        assertThrows(IllegalArgumentException.class, () ->
                employeeService.addEmployee("", "valid@ems.com", "Dev", deptId, joiningDate, "Pass123", "Pass123")
        );

        // Step N: Try logging in using the newly created employee's official email and temporary password
        User authenticatedUser = authService.authenticate(testEmail, testPassword);
        assertNotNull(authenticatedUser, "Step N: Authentication must succeed for new employee credentials");
        assertEquals("EMPLOYEE", authenticatedUser.getRole(), "Authenticated user role must be EMPLOYEE");

        // Step O: Confirm role routes to Employee Dashboard
        assertFalse(authenticatedUser.isAdmin(), "Step O: Non-admin employee must route to Employee Dashboard");
    }
}
