package com.ems.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an authenticated user account within the system, mapped to the 'users' table.
 */
public class User {

    private Long userId;
    private String officialEmail;
    private String passwordHash;
    private String role;
    private String status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String officialEmail, String passwordHash, String role, String status) {
        this.officialEmail = officialEmail;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public User(Long userId, String officialEmail, String passwordHash, String role, String status,
                LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.officialEmail = officialEmail;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
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
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
               Objects.equals(officialEmail, user.officialEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, officialEmail);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", officialEmail='" + officialEmail + '\'' +
                ", passwordHash='[PROTECTED]'" +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", lastLogin=" + lastLogin +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
