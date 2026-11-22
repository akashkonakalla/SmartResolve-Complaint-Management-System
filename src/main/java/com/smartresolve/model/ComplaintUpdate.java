package com.smartresolve.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ComplaintUpdate represents an immutable entry in the complaint audit trail,
 * recording who transitioned the status, when, and with what remarks.
 */
public class ComplaintUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int complaintId;
    private int updatedBy;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private Timestamp createdAt;

    // Joined user attributes
    private String updatedByName;
    private String updatedByRole;

    public ComplaintUpdate() {
    }

    public ComplaintUpdate(int complaintId, int updatedBy, String oldStatus, String newStatus, String remarks) {
        this.complaintId = complaintId;
        this.updatedBy = updatedBy;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getUpdatedByRole() {
        return updatedByRole;
    }

    public void setUpdatedByRole(String updatedByRole) {
        this.updatedByRole = updatedByRole;
    }

    public String getStatusBadgeClass() {
        if (newStatus == null) return "bg-secondary";
        switch (newStatus.toUpperCase()) {
            case "NEW": return "bg-secondary";
            case "ASSIGNED": return "bg-info text-dark";
            case "IN_PROGRESS": return "bg-warning text-dark";
            case "RESOLVED": return "bg-success";
            case "CLOSED": return "bg-dark";
            default: return "bg-primary";
        }
    }

    public String getStatusIcon() {
        if (newStatus == null) return "bi-record-circle";
        switch (newStatus.toUpperCase()) {
            case "NEW": return "bi-plus-circle text-secondary";
            case "ASSIGNED": return "bi-person-check text-info";
            case "IN_PROGRESS": return "bi-gear-wide-connected text-warning";
            case "RESOLVED": return "bi-check2-circle text-success";
            case "CLOSED": return "bi-lock text-dark";
            default: return "bi-record-circle text-primary";
        }
    }
}
