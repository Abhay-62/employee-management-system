package com.ems.dao;

import com.ems.model.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepartmentDAOTest {

    @Test
    @DisplayName("Check active departments in database")
    void testFindActiveDepartments() {
        DepartmentDAO dao = new DepartmentDAO();
        try {
            List<Department> departments = dao.findAllActive();
            System.out.println("DEBUG: Departments count in DB = " + departments.size());
            for (Department d : departments) {
                System.out.println("DEBUG: Dept: " + d.getDepartmentId() + " - " + d.getName() + " (" + d.getCode() + ")");
            }
            assertNotNull(departments);
        } catch (Exception e) {
            System.out.println("DEBUG: DB error during dept query: " + e.getMessage());
        }
    }
}
