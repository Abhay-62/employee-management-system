package com.ems.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object combining official employee details, user account info,
 * department details, and optional personal contact information.
 */
public class EmployeeProfileDTO {

    // 1. Basic / Official Information
    private long employeeId;
    private long userId;
    private long departmentId;
    private String employeeCode;
    private String fullName;
    private String designation;
    private String departmentName;
    private String departmentCode;
    private LocalDate joiningDate;
    private String profilePhoto;
    private String employeeStatus;
    private String officialEmail;
    private String userStatus;

    // 2. Personal Information
    private String personalPhone;
    private String personalEmail;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
    private LocalDateTime personalInfoUpdatedAt;

    public EmployeeProfileDTO() {
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public LocalDateTime getPersonalInfoUpdatedAt() {
        return personalInfoUpdatedAt;
    }

    public void setPersonalInfoUpdatedAt(LocalDateTime personalInfoUpdatedAt) {
        this.personalInfoUpdatedAt = personalInfoUpdatedAt;
    }

    /**
     * Checks whether any personal info field has been recorded.
     */
    public boolean hasPersonalInfo() {
        return (personalPhone != null && !personalPhone.isBlank())
                || (personalEmail != null && !personalEmail.isBlank())
                || (address != null && !address.isBlank())
                || (emergencyContactName != null && !emergencyContactName.isBlank())
                || (emergencyContactPhone != null && !emergencyContactPhone.isBlank())
                || (emergencyContactRelation != null && !emergencyContactRelation.isBlank());
    }

    @Override
    public String toString() {
        return "EmployeeProfileDTO{" +
                "employeeId=" + employeeId +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", designation='" + designation + '\'' +
                ", officialEmail='" + officialEmail + '\'' +
                '}';
    }
}
