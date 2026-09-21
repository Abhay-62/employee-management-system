package com.ems.dto;

import java.time.LocalDate;

/**
 * Data transfer object representing a recent employee record for dashboard displays.
 */
public class RecentEmployeeDTO {

    private final Long employeeId;
    private final String employeeCode;
    private final String fullName;
    private final String departmentName;
    private final String designation;
    private final LocalDate joiningDate;
    private final String status;

    public RecentEmployeeDTO(Long employeeId, String employeeCode, String fullName,
                             String departmentName, String designation, LocalDate joiningDate, String status) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.departmentName = departmentName;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getDesignation() {
        return designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public String getStatus() {
        return status;
    }
}
