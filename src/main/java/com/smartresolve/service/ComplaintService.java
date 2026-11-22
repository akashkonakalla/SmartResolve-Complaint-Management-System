package com.smartresolve.service;

import com.smartresolve.dao.CategoryDAO;
import com.smartresolve.dao.ComplaintDAO;
import com.smartresolve.model.Category;
import com.smartresolve.model.Complaint;
import com.smartresolve.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * ComplaintService implements lifecycle transitions and operational rules for complaints.
 */
public class ComplaintService {

    private final ComplaintDAO complaintDAO;
    private final CategoryDAO categoryDAO;

    public ComplaintService() {
        this.complaintDAO = new ComplaintDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public ComplaintService(ComplaintDAO complaintDAO, CategoryDAO categoryDAO) {
        this.complaintDAO = complaintDAO;
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    /**
     * Submits a new user service request with input validation and transactional persistence.
     */
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

        // Validate priority
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

    public List<Complaint> getUserComplaints(int userId) throws SQLException {
        return complaintDAO.findByUserId(userId);
    }

    public Complaint getComplaintById(int id) throws SQLException {
        return complaintDAO.findById(id);
    }
}
