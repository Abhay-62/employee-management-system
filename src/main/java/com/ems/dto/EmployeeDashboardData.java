package com.ems.dto;

/**
 * Aggregated profile and document status data for the Employee Dashboard.
 */
public class EmployeeDashboardData {

    private final boolean hasProfile;
    private final Long employeeId;
    private final String employeeCode;
    private final String fullName;
    private final String departmentName;
    private final String designation;
    private final String accountStatus;
    private final long pendingDocuments;
    private final long approvedDocuments;
    private final long pendingRequests;

    public EmployeeDashboardData(boolean hasProfile, Long employeeId, String employeeCode,
                                 String fullName, String departmentName, String designation,
                                 String accountStatus, long pendingDocuments, long approvedDocuments,
                                 long pendingRequests) {
        this.hasProfile = hasProfile;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.departmentName = departmentName;
        this.designation = designation;
        this.accountStatus = accountStatus;
        this.pendingDocuments = pendingDocuments;
        this.approvedDocuments = approvedDocuments;
        this.pendingRequests = pendingRequests;
    }

    public static EmployeeDashboardData pendingProfile(String accountStatus) {
        return new EmployeeDashboardData(
                false, null, "PENDING", "Setup Pending", "Unassigned", "Pending Assignment",
                accountStatus != null ? accountStatus : "ACTIVE", 0, 0, 0
        );
    }

    public boolean hasProfile() {
        return hasProfile;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public long getPendingDocuments() {
        return pendingDocuments;
    }

    public long getApprovedDocuments() {
        return approvedDocuments;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }
}
