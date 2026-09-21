package com.ems.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a department entity from the departments table.
 */
public class Department {

    private Long departmentId;
    private String name;
    private String code;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Department() {
    }

    public Department(Long departmentId, String name, String code, String description, String status) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public Department(Long departmentId, String name, String code, String description,
                      String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.departmentId = departmentId;
        this.name = name;
        this.code = code;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(departmentId, that.departmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId);
    }

    @Override
    public String toString() {
        if (code != null && !code.isBlank()) {
            return name + " (" + code + ")";
        }
        return name != null ? name : "";
    }
}
