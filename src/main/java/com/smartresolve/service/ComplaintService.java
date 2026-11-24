package com.smartresolve.service;

import com.smartresolve.dao.CategoryDAO;
import com.smartresolve.dao.ComplaintDAO;
import com.smartresolve.dao.ComplaintUpdateDAO;
import com.smartresolve.dao.UserDAO;
import com.smartresolve.model.Category;
import com.smartresolve.model.Complaint;
import com.smartresolve.model.ComplaintUpdate;
import com.smartresolve.model.User;
import com.smartresolve.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * ComplaintService implements lifecycle transitions and operational rules for complaints.
 */
public class ComplaintService {

    private final ComplaintDAO complaintDAO;
    private final CategoryDAO categoryDAO;
    private final ComplaintUpdateDAO complaintUpdateDAO;
    private final UserDAO userDAO;

    public ComplaintService() {
        this.complaintDAO = new ComplaintDAO();
        this.categoryDAO = new CategoryDAO();
        this.complaintUpdateDAO = new ComplaintUpdateDAO();
        this.userDAO = new UserDAO();
    }

    public ComplaintService(ComplaintDAO complaintDAO, CategoryDAO categoryDAO, 
                            ComplaintUpdateDAO complaintUpdateDAO, UserDAO userDAO) {
        this.complaintDAO = complaintDAO;
        this.categoryDAO = categoryDAO;
        this.complaintUpdateDAO = complaintUpdateDAO;
        this.userDAO = userDAO;
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public Complaint submitComplaint(int userId, int categoryId, String title, 
                                    String description, String location, String priority, 
                                    String imagePath) throws IllegalArgumentException, SQLException {

        String sanitizedTitle = ValidationUtil.sanitize(title);
        String sanitizedDesc = ValidationUtil.sanitize(description);
        String sanitizedLoc = ValidationUtil.sanitize(location);

        if (!ValidationUtil.isNotEmpty(sanitizedTitle)) {
            throw new IllegalArgumentException("Complaint title is required.");
        }
        if (sanitizedTitle.length() < 5 || sanitizedTitle.length() > 150) {
            throw new IllegalArgumentException("Title must be between 5 and 150 characters.");
        }
        if (!ValidationUtil.isNotEmpty(sanitizedDesc)) {
            throw new IllegalArgumentException("Description of the issue is required.");
        }
        if (sanitizedDesc.length() < 10) {
            throw new IllegalArgumentException("Please provide a detailed description (at least 10 characters).");
        }
        if (!ValidationUtil.isNotEmpty(sanitizedLoc)) {
            throw new IllegalArgumentException("Location (room, floor, or block) is required.");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Please select a valid complaint category.");
        }

        String cleanPriority = Complaint.PRIORITY_MEDIUM;
        if (priority != null) {
            String pUpper = priority.trim().toUpperCase();
            if (Complaint.PRIORITY_LOW.equals(pUpper) || Complaint.PRIORITY_MEDIUM.equals(pUpper) ||
                Complaint.PRIORITY_HIGH.equals(pUpper) || Complaint.PRIORITY_URGENT.equals(pUpper)) {
                cleanPriority = pUpper;
            }
        }

        Complaint complaint = new Complaint(userId, categoryId, sanitizedTitle, sanitizedDesc, sanitizedLoc, cleanPriority);
        complaint.setImagePath(imagePath);

        int generatedId = complaintDAO.createWithTransaction(complaint);
        complaint.setId(generatedId);

        return complaint;
    }

    public boolean assignTechnician(int complaintId, int technicianId, int adminId, String remarks) 
            throws IllegalArgumentException, SQLException {

        Complaint complaint = complaintDAO.findById(complaintId);
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint not found.");
        }

        User technician = userDAO.findById(technicianId);
        if (technician == null || !User.ROLE_TECHNICIAN.equalsIgnoreCase(technician.getRole())) {
            throw new IllegalArgumentException("Selected user is not a valid technician.");
        }

        String note = (remarks != null && !remarks.trim().isEmpty()) 
                      ? remarks.trim() 
                      : "Assigned to " + technician.getFullName() + " for service resolution.";

        return complaintDAO.assignTechnicianWithTransaction(complaintId, technicianId, adminId, complaint.getStatus(), note);
    }

    /**
     * Executes validated state transitions across the complaint lifecycle:
     * ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED, or Admin reopening.
     */
    public boolean updateComplaintStatus(int complaintId, String targetStatus, int updaterId, 
                                        String updaterRole, String remarks) 
            throws IllegalArgumentException, SQLException {

        Complaint complaint = complaintDAO.findById(complaintId);
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint record not found.");
        }

        String currentStatus = complaint.getStatus();
        String newStatus = targetStatus.toUpperCase().trim();

        // Security check: Technician can only update their own assigned complaints
        if (User.ROLE_TECHNICIAN.equalsIgnoreCase(updaterRole)) {
            if (complaint.getAssignedTo() == null || complaint.getAssignedTo() != updaterId) {
                throw new IllegalArgumentException("You are not authorized to update complaints assigned to another technician.");
            }
            // Technician valid transitions:
            // ASSIGNED -> IN_PROGRESS
            // IN_PROGRESS -> RESOLVED
            if (Complaint.STATUS_ASSIGNED.equals(currentStatus) && !Complaint.STATUS_IN_PROGRESS.equals(newStatus)) {
                throw new IllegalArgumentException("Work order must be moved to IN_PROGRESS before marking resolved.");
            }
            if (Complaint.STATUS_IN_PROGRESS.equals(currentStatus) && !Complaint.STATUS_RESOLVED.equals(newStatus)) {
                throw new IllegalArgumentException("In-progress tasks can only be transitioned to RESOLVED.");
            }
            if (Complaint.STATUS_RESOLVED.equals(currentStatus) || Complaint.STATUS_CLOSED.equals(currentStatus)) {
                throw new IllegalArgumentException("Complaint is already resolved or closed.");
            }
        }

        String note = (remarks != null && !remarks.trim().isEmpty()) 
                      ? remarks.trim() 
                      : "Status updated to " + newStatus;

        return complaintDAO.updateStatusWithTransaction(complaintId, newStatus, updaterId, currentStatus, note);
    }

    public List<Complaint> getUserComplaints(int userId) throws SQLException {
        return complaintDAO.findByUserId(userId);
    }

    public Complaint getComplaintById(int id) throws SQLException {
        return complaintDAO.findById(id);
    }

    public List<ComplaintUpdate> getComplaintHistory(int complaintId) throws SQLException {
        return complaintUpdateDAO.findByComplaintId(complaintId);
    }
}
