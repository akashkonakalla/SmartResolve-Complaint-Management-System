package com.smartresolve.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User represents an actor in the SmartResolve platform.
 * Supports USER, ADMIN, and TECHNICIAN roles.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // Role constants
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TECHNICIAN = "TECHNICIAN";

    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private String role;
    private Timestamp createdAt;

    public User() {
        this.role = ROLE_USER;
    }

    public User(String fullName, String email, String passwordHash, String phone, String role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role != null ? role : ROLE_USER;
    }

    public User(int id, String fullName, String email, String passwordHash, String phone, String role, Timestamp createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role != null ? role : ROLE_USER;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Role helper methods
    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(this.role);
    }

    public boolean isTechnician() {
        return ROLE_TECHNICIAN.equalsIgnoreCase(this.role);
    }

    public boolean isUser() {
        return ROLE_USER.equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        // Exclude passwordHash for security reasons
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
