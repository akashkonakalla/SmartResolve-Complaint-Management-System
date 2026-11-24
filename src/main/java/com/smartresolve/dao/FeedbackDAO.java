package com.smartresolve.dao;

import com.smartresolve.model.ComplaintUpdate;
import com.smartresolve.model.Feedback;
import com.smartresolve.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FeedbackDAO manages persistent CRUD operations for user satisfaction ratings and feedback.
 */
public class FeedbackDAO {

    private static final Logger LOGGER = Logger.getLogger(FeedbackDAO.class.getName());

    private static final String SQL_INSERT = 
        "INSERT INTO feedback (complaint_id, user_id, rating, comments) VALUES (?, ?, ?, ?)";

    private static final String SQL_FIND_BY_COMPLAINT_ID = 
        "SELECT f.id, f.complaint_id, f.user_id, f.rating, f.comments, f.created_at, " +
        "       u.full_name AS user_name, u.email AS user_email, " +
        "       c.complaint_number, c.title AS complaint_title " +
        "FROM feedback f " +
        "INNER JOIN users u ON f.user_id = u.id " +
        "INNER JOIN complaints c ON f.complaint_id = c.id " +
        "WHERE f.complaint_id = ?";

    private static final String SQL_AVG_RATING = 
        "SELECT COALESCE(AVG(rating), 0.0) AS avg_rating, COUNT(*) AS total_count FROM feedback";

    private static final String SQL_FIND_RECENT = 
        "SELECT f.id, f.complaint_id, f.user_id, f.rating, f.comments, f.created_at, " +
        "       u.full_name AS user_name, u.email AS user_email, " +
        "       c.complaint_number, c.title AS complaint_title " +
        "FROM feedback f " +
        "INNER JOIN users u ON f.user_id = u.id " +
        "INNER JOIN complaints c ON f.complaint_id = c.id " +
        "ORDER BY f.created_at DESC LIMIT ?";

    /**
     * Submits user feedback within an active database transaction.
     */
    public int create(Feedback feedback, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, feedback.getComplaintId());
            ps.setInt(2, feedback.getUserId());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComments());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Finds feedback record for a given complaint ID.
     */
    public Feedback findByComplaintId(int complaintId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_COMPLAINT_ID)) {

            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeedback(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving feedback for complaint ID: " + complaintId, e);
            throw e;
        }
        return null;
    }

    /**
     * Computes the overall average customer rating.
     */
    public double getAverageRating() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_AVG_RATING);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return Math.round(rs.getDouble("avg_rating") * 10.0) / 10.0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average rating", e);
            throw e;
        }
        return 0.0;
    }

    /**
     * Retrieves recent user feedback entries.
     */
    public List<Feedback> findRecent(int limit) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_RECENT)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching recent feedback", e);
            throw e;
        }
        return list;
    }

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setId(rs.getInt("id"));
        f.setComplaintId(rs.getInt("complaint_id"));
        f.setUserId(rs.getInt("user_id"));
        f.setRating(rs.getInt("rating"));
        f.setComments(rs.getString("comments"));
        f.setCreatedAt(rs.getTimestamp("created_at"));
        f.setUserName(rs.getString("user_name"));
        f.setUserEmail(rs.getString("user_email"));
        f.setComplaintNumber(rs.getString("complaint_number"));
        f.setComplaintTitle(rs.getString("complaint_title"));
        return f;
    }
}
