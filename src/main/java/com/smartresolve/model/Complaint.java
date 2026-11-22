package com.smartresolve.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Complaint represents a user service request tracked through its lifecycle.
 */
public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    // Status Constants
    public static final String STATUS_NEW = "NEW";
    public static final String STATUS_ASSIGNED = "ASSIGNED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_RESOLVED = "RESOLVED";
    public static final String STATUS_CLOSED = "CLOSED";

    // Priority Constants
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";

    private int id;
    private String complaintNumber;
    private int userId;
    private int categoryId;
    private String title;
    private String description;
    private String location;
    private String priority;
    private String status;
    private Integer assignedTo; // Nullable technician ID
    private String imagePath;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp resolvedAt;

    // Joined view attributes
    private String userName;
    private String userEmail;
    private String categoryName;
    private String technicianName;

    public Complaint() {
        this.status = STATUS_NEW;
        this.priority = PRIORITY_MEDIUM;
    }

    public Complaint(int userId, int categoryId, String title, String description, String location, String priority) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.priority = priority != null ? priority : PRIORITY_MEDIUM;
        this.status = STATUS_NEW;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    // Helper status inquiries
    public boolean isNew() {
        return STATUS_NEW.equalsIgnoreCase(this.status);
    }

    public boolean isAssigned() {
        return STATUS_ASSIGNED.equalsIgnoreCase(this.status);
    }

    public boolean isInProgress() {
        return STATUS_IN_PROGRESS.equalsIgnoreCase(this.status);
    }

    public boolean isResolved() {
        return STATUS_RESOLVED.equalsIgnoreCase(this.status);
    }

    public boolean isClosed() {
        return STATUS_CLOSED.equalsIgnoreCase(this.status);
    }

    public String getStatusBadgeClass() {
        switch (this.status) {
            case STATUS_NEW:
                return "bg-secondary";
            case STATUS_ASSIGNED:
                return "bg-info text-dark";
            case STATUS_IN_PROGRESS:
                return "bg-warning text-dark";
            case STATUS_RESOLVED:
                return "bg-success";
            case STATUS_CLOSED:
                return "bg-dark";
            default:
                return "bg-light text-dark";
        }
    }

    public String getPriorityBadgeClass() {
        switch (this.priority) {
            case PRIORITY_URGENT:
                return "badge bg-danger";
            case PRIORITY_HIGH:
                return "badge bg-warning text-dark";
            case PRIORITY_MEDIUM:
                return "badge bg-primary";
            case PRIORITY_LOW:
                return "badge bg-info text-dark";
            default:
                return "badge bg-secondary";
        }
    }
}
