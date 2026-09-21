package com.ems.service;

import com.ems.dao.DepartmentDAO;
import com.ems.model.Department;

import java.sql.SQLException;
import java.util.List;

/**
 * Service managing department operations.
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this.departmentDAO = new DepartmentDAO();
    }

    public DepartmentService(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    /**
     * Retrieves all active departments from the database.
     * Seeds initial baseline departments if the table is currently empty.
     *
     * @return list of active Department entities
     */
    public List<Department> getActiveDepartments() {
        try {
            List<Department> list = departmentDAO.findAllActive();
            if (list.isEmpty()) {
                departmentDAO.seedDefaultDepartmentsIfEmpty();
                list = departmentDAO.findAllActive();
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load active departments from database.", e);
        }
    }
}
