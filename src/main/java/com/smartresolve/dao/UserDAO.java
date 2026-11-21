package com.smartresolve.dao;

import com.smartresolve.model.User;
import com.smartresolve.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserDAO handles all JDBC database operations for User entities.
 * Enforces parameterized queries using PreparedStatement to prevent SQL injection.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    private static final String SQL_INSERT = 
        "INSERT INTO users (full_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID = 
        "SELECT id, full_name, email, password_hash, phone, role, created_at FROM users WHERE id = ?";

    private static final String SQL_FIND_BY_EMAIL = 
        "SELECT id, full_name, email, password_hash, phone, role, created_at FROM users WHERE email = ?";

    private static final String SQL_COUNT_BY_EMAIL = 
        "SELECT COUNT(*) FROM users WHERE email = ?";

    private static final String SQL_FIND_BY_ROLE = 
        "SELECT id, full_name, email, password_hash, phone, role, created_at FROM users WHERE role = ? ORDER BY full_name ASC";

    private static final String SQL_FIND_ALL = 
        "SELECT id, full_name, email, password_hash, phone, role, created_at FROM users ORDER BY created_at DESC";

    /**
     * Inserts a new user record into the database.
     * 
     * @param user user entity containing registration details
     * @return generated user ID upon successful creation, or -1 if failed
     * @throws SQLException if a database access error occurs
     */
    public int create(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User entity cannot be null.");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole() != null ? user.getRole() : User.ROLE_USER);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating user failed, no generated ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting user with email: " + user.getEmail(), e);
            throw e;
        }
    }

    /**
     * Finds a user by their primary key ID.
     */
    public User findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by ID: " + id, e);
            throw e;
        }
        return null;
    }

    /**
     * Finds a user by their unique email address.
     */
    public User findByEmail(String email) throws SQLException {
        if (email == null) {
            return null;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by email: " + email, e);
            throw e;
        }
        return null;
    }

    /**
     * Checks if an email is already registered in the system.
     */
    public boolean emailExists(String email) throws SQLException {
        if (email == null) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_EMAIL)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence: " + email, e);
            throw e;
        }
        return false;
    }

    /**
     * Retrieves all users matching a specific role (e.g., TECHNICIAN).
     */
    public List<User> findByRole(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ROLE)) {

            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving users by role: " + role, e);
            throw e;
        }
        return list;
    }

    /**
     * Retrieves all registered users in descending registration order.
     */
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            throw e;
        }
        return list;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
