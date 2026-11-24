package com.smartresolve.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Feedback represents user evaluation and satisfaction rating
 * recorded upon complaint resolution.
 */
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int complaintId;
    private int userId;
    private int rating; // 1 to 5
    private String comments;
    private Timestamp createdAt;

    // Joined / presentation fields
    private String userName;
    private String userEmail;
    private String complaintNumber;
    private String complaintTitle;

    public Feedback() {}

    public Feedback(int complaintId, int userId, int rating, String comments) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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

    public String getComplaintNumber() {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber) {
        this.complaintNumber = complaintNumber;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public String getRatingLabel() {
        switch (rating) {
            case 5: return "Excellent";
            case 4: return "Good";
            case 3: return "Average";
            case 2: return "Below Average";
            case 1: return "Poor";
            default: return "Unrated";
        }
    }
}
