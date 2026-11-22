package com.smartresolve.dao;

import com.smartresolve.model.Category;
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
 * CategoryDAO handles database access for complaint categories.
 */
public class CategoryDAO {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    private static final String SQL_FIND_ALL = 
        "SELECT id, name, description, created_at FROM categories ORDER BY name ASC";

    private static final String SQL_FIND_BY_ID = 
        "SELECT id, name, description, created_at FROM categories WHERE id = ?";

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setDescription(rs.getString("description"));
                cat.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(cat);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving categories", e);
            throw e;
        }
        return list;
    }

    public Category findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category cat = new Category();
                    cat.setId(rs.getInt("id"));
                    cat.setName(rs.getString("name"));
                    cat.setDescription(rs.getString("description"));
                    cat.setCreatedAt(rs.getTimestamp("created_at"));
                    return cat;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving category by ID: " + id, e);
            throw e;
        }
        return null;
    }
}
