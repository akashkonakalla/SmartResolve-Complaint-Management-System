package com.smartresolve.dao;

import com.smartresolve.model.Complaint;
import com.smartresolve.util.DBConnection;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ComplaintDAO executes JDBC database operations for complaints.
 * Coordinates transactional insertions and audit trail updates.
 */
public class ComplaintDAO {

    private static final Logger LOGGER = Logger.getLogger(ComplaintDAO.class.getName());

    private static final String SQL_INSERT = 
        "INSERT INTO complaints (complaint_number, user_id, category_id, title, description, location, priority, status, image_path) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_UPDATE = 
        "INSERT INTO complaint_updates (complaint_id, updated_by, old_status, new_status, remarks) " +
        "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_JOIN = 
        "SELECT c.id, c.complaint_number, c.user_id, c.category_id, c.title, c.description, c.location, " +
        "       c.priority, c.status, c.assigned_to, c.image_path, c.created_at, c.updated_at, c.resolved_at, " +
        "       u.full_name AS user_name, u.email AS user_email, cat.name AS category_name, " +
        "       t.full_name AS technician_name " +
        "FROM complaints c " +
        "INNER JOIN users u ON c.user_id = u.id " +
        "INNER JOIN categories cat ON c.category_id = cat.id " +
        "LEFT JOIN users t ON c.assigned_to = t.id ";

    private static final String SQL_FIND_BY_ID = 
        SQL_SELECT_JOIN + "WHERE c.id = ?";

    private static final String SQL_FIND_BY_USER_ID = 
        SQL_SELECT_JOIN + "WHERE c.user_id = ? ORDER BY c.created_at DESC";

    private static final String SQL_FIND_RECENT_BY_USER_ID = 
        SQL_SELECT_JOIN + "WHERE c.user_id = ? ORDER BY c.created_at DESC LIMIT ?";

    private static final String SQL_FIND_RECENT_ALL = 
        SQL_SELECT_JOIN + "ORDER BY c.created_at DESC LIMIT ?";

    private static final String SQL_USER_STATS = 
        "SELECT " +
        "  COUNT(*) AS total, " +
        "  SUM(CASE WHEN status IN ('NEW', 'ASSIGNED') THEN 1 ELSE 0 END) AS pending, " +
        "  SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress, " +
        "  SUM(CASE WHEN status IN ('RESOLVED', 'CLOSED') THEN 1 ELSE 0 END) AS resolved " +
        "FROM complaints WHERE user_id = ?";

    private static final String SQL_ADMIN_STATS = 
        "SELECT " +
        "  COUNT(*) AS total, " +
        "  SUM(CASE WHEN status = 'NEW' THEN 1 ELSE 0 END) AS new_complaints, " +
        "  SUM(CASE WHEN status = 'ASSIGNED' THEN 1 ELSE 0 END) AS assigned, " +
        "  SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress, " +
        "  SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved, " +
        "  SUM(CASE WHEN status = 'CLOSED' THEN 1 ELSE 0 END) AS closed " +
        "FROM complaints";

    private static final String SQL_TECH_WORKLOADS = 
        "SELECT u.id, u.full_name, " +
        "       COUNT(c.id) AS active_load " +
        "FROM users u " +
        "LEFT JOIN complaints c ON u.id = c.assigned_to AND c.status IN ('ASSIGNED', 'IN_PROGRESS') " +
        "WHERE u.role = 'TECHNICIAN' " +
        "GROUP BY u.id, u.full_name " +
        "ORDER BY active_load ASC, u.full_name ASC";

    public int createWithTransaction(Complaint complaint) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String complaintNumber = generateComplaintNumber(conn);
            complaint.setComplaintNumber(complaintNumber);

            int complaintId;
            try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, complaint.getComplaintNumber());
                ps.setInt(2, complaint.getUserId());
                ps.setInt(3, complaint.getCategoryId());
                ps.setString(4, complaint.getTitle());
                ps.setString(5, complaint.getDescription());
                ps.setString(6, complaint.getLocation());
                ps.setString(7, complaint.getPriority() != null ? complaint.getPriority() : Complaint.PRIORITY_MEDIUM);
                ps.setString(8, Complaint.STATUS_NEW);
                ps.setString(9, complaint.getImagePath());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        complaintId = rs.getInt(1);
                        complaint.setId(complaintId);
                    } else {
                        throw new SQLException("Creating complaint failed, no ID returned.");
                    }
                }
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(SQL_INSERT_UPDATE)) {
                psUpdate.setInt(1, complaintId);
                psUpdate.setInt(2, complaint.getUserId());
                psUpdate.setString(3, "NONE");
                psUpdate.setString(4, Complaint.STATUS_NEW);
                psUpdate.setString(5, "Complaint submitted by user.");
                psUpdate.executeUpdate();
            }

            conn.commit();
            LOGGER.info("Complaint created successfully with ID: " + complaintId + " Number: " + complaintNumber);
            return complaintId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back for complaint creation.");
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to rollback transaction", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.FINE, "Error closing connection", e);
                }
            }
        }
    }

    public Complaint findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving complaint ID: " + id, e);
            throw e;
        }
        return null;
    }

    public List<Complaint> findByUserId(int userId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving complaints for user ID: " + userId, e);
            throw e;
        }
        return list;
    }

    public List<Complaint> findRecentByUserId(int userId, int limit) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_RECENT_BY_USER_ID)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit > 0 ? limit : 5);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent complaints for user ID: " + userId, e);
            throw e;
        }
        return list;
    }

    public List<Complaint> findRecentAll(int limit) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_RECENT_ALL)) {

            ps.setInt(1, limit > 0 ? limit : 10);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recent complaints for admin", e);
            throw e;
        }
        return list;
    }

    public Map<String, Integer> getUserStatistics(int userId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("inProgress", 0);
        stats.put("resolved", 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_USER_STATS)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total", rs.getInt("total"));
                    stats.put("pending", rs.getInt("pending"));
                    stats.put("inProgress", rs.getInt("in_progress"));
                    stats.put("resolved", rs.getInt("resolved"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating statistics for user ID: " + userId, e);
            throw e;
        }
        return stats;
    }

    public Map<String, Integer> getAdminStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("newComplaints", 0);
        stats.put("assigned", 0);
        stats.put("inProgress", 0);
        stats.put("resolved", 0);
        stats.put("closed", 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ADMIN_STATS);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("newComplaints", rs.getInt("new_complaints"));
                stats.put("assigned", rs.getInt("assigned"));
                stats.put("inProgress", rs.getInt("in_progress"));
                stats.put("resolved", rs.getInt("resolved"));
                stats.put("closed", rs.getInt("closed"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating admin statistics", e);
            throw e;
        }
        return stats;
    }

    public List<Map<String, Object>> getTechnicianWorkloads() throws SQLException {
        List<Map<String, Object>> workloads = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TECH_WORKLOADS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("technicianId", rs.getInt("id"));
                map.put("technicianName", rs.getString("full_name"));
                map.put("activeLoad", rs.getInt("active_load"));
                workloads.add(map);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving technician workloads", e);
            throw e;
        }
        return workloads;
    }

    /**
     * Executes dynamic, parameterized search and multi-facet filtering.
     * Guaranteed SQL injection safe via PreparedStatement positional parameters.
     */
    public List<Complaint> searchAndFilter(String keyword, String status, Integer categoryId, String priority) 
            throws SQLException {

        StringBuilder sql = new StringBuilder(SQL_SELECT_JOIN);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String term = "%" + keyword.trim() + "%";
            sql.append("AND (c.complaint_number LIKE ? OR c.title LIKE ? OR c.description LIKE ? OR c.location LIKE ? OR u.full_name LIKE ?) ");
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND c.status = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND c.category_id = ? ");
            params.add(categoryId);
        }

        if (priority != null && !priority.trim().isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            sql.append("AND c.priority = ? ");
            params.add(priority.trim().toUpperCase());
        }

        sql.append("ORDER BY c.created_at DESC");

        List<Complaint> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching and filtering complaints", e);
            throw e;
        }
        return list;
    }

    private String generateComplaintNumber(Connection conn) throws SQLException {
        int currentYear = Year.now().getValue();
        String prefix = "SR-" + currentYear + "-";
        String countSql = "SELECT COUNT(*) FROM complaints WHERE complaint_number LIKE ?";

        int nextSeq = 1;
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nextSeq = rs.getInt(1) + 1;
                }
            }
        }
        return String.format("%s%04d", prefix, nextSeq);
    }

    private Complaint mapRowToComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setComplaintNumber(rs.getString("complaint_number"));
        c.setUserId(rs.getInt("user_id"));
        c.setCategoryId(rs.getInt("category_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setLocation(rs.getString("location"));
        c.setPriority(rs.getString("priority"));
        c.setStatus(rs.getString("status"));

        int assigned = rs.getInt("assigned_to");
        if (!rs.wasNull()) {
            c.setAssignedTo(assigned);
        }

        c.setImagePath(rs.getString("image_path"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        c.setResolvedAt(rs.getTimestamp("resolved_at"));

        c.setUserName(rs.getString("user_name"));
        c.setUserEmail(rs.getString("user_email"));
        c.setCategoryName(rs.getString("category_name"));
        c.setTechnicianName(rs.getString("technician_name"));

        return c;
    }
}
