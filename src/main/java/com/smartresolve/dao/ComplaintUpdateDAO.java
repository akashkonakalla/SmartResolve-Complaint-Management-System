package com.smartresolve.dao;

import com.smartresolve.model.ComplaintUpdate;
import com.smartresolve.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ComplaintUpdateDAO manages the persistent audit trail of complaint updates and status changes.
 */
public class ComplaintUpdateDAO {

    private static final Logger LOGGER = Logger.getLogger(ComplaintUpdateDAO.class.getName());

    private static final String SQL_INSERT = 
        "INSERT INTO complaint_updates (complaint_id, updated_by, old_status, new_status, remarks) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_COMPLAINT_ID = 
        "SELECT cu.id, cu.complaint_id, cu.updated_by, cu.old_status, cu.new_status, cu.remarks, cu.created_at, " +
        "       u.full_name AS updated_by_name, u.role AS updated_by_role " +
        "FROM complaint_updates cu " +
        "INNER JOIN users u ON cu.updated_by = u.id " +
        "WHERE cu.complaint_id = ? " +
        "ORDER BY cu.created_at ASC";

    public int create(ComplaintUpdate update, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, update.getComplaintId());
            ps.setInt(2, update.getUpdatedBy());
            ps.setString(3, update.getOldStatus());
            ps.setString(4, update.getNewStatus());
            ps.setString(5, update.getRemarks());
            return ps.executeUpdate();
        }
    }

    public List<ComplaintUpdate> findByComplaintId(int complaintId) throws SQLException {
        List<ComplaintUpdate> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_COMPLAINT_ID)) {

            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintUpdate u = new ComplaintUpdate();
                    u.setId(rs.getInt("id"));
                    u.setComplaintId(rs.getInt("complaint_id"));
                    u.setUpdatedBy(rs.getInt("updated_by"));
                    u.setOldStatus(rs.getString("old_status"));
                    u.setNewStatus(rs.getString("new_status"));
                    u.setRemarks(rs.getString("remarks"));
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    u.setUpdatedByName(rs.getString("updated_by_name"));
                    u.setUpdatedByRole(rs.getString("updated_by_role"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving status history for complaint ID: " + complaintId, e);
            throw e;
        }
        return list;
    }
}
