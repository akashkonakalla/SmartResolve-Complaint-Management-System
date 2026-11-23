package com.smartresolve.controller;

import com.smartresolve.dao.CategoryDAO;
import com.smartresolve.dao.ComplaintDAO;
import com.smartresolve.dao.UserDAO;
import com.smartresolve.model.Category;
import com.smartresolve.model.Complaint;
import com.smartresolve.model.User;
import com.smartresolve.service.ComplaintService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AdminServlet orchestrates the administrator management portal,
 * system-wide complaint oversight, technician workload monitoring,
 * multi-criteria search, filtering, and technician dispatch/assignment.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/complaints", "/admin/complaints/assign", "/admin/users"})
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());

    private ComplaintDAO complaintDAO;
    private CategoryDAO categoryDAO;
    private UserDAO userDAO;
    private ComplaintService complaintService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintDAO = new ComplaintDAO();
        this.categoryDAO = new CategoryDAO();
        this.userDAO = new UserDAO();
        this.complaintService = new ComplaintService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/dashboard".equals(path)) {
            handleDashboard(request, response);
        } else if ("/admin/complaints".equals(path)) {
            handleComplaints(request, response);
        } else if ("/admin/users".equals(path)) {
            handleUsers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/admin/complaints/assign".equals(path) || "assign".equals(request.getParameter("action"))) {
            handleAssignTechnician(request, response);
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            Map<String, Integer> stats = complaintDAO.getAdminStatistics();
            List<Complaint> recentComplaints = complaintDAO.findRecentAll(8);
            List<Map<String, Object>> workloads = complaintDAO.getTechnicianWorkloads();
            List<User> technicians = userDAO.findByRole(User.ROLE_TECHNICIAN);

            request.setAttribute("stats", stats);
            request.setAttribute("recentComplaints", recentComplaints);
            request.setAttribute("workloads", workloads);
            request.setAttribute("technicians", technicians);

            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load admin dashboard data", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load administrator dashboard.");
        }
    }

    private void handleComplaints(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String categoryIdStr = request.getParameter("categoryId");
        String priority = request.getParameter("priority");

        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            List<Complaint> complaints = complaintDAO.searchAndFilter(keyword, status, categoryId, priority);
            List<Category> categories = categoryDAO.findAll();
            List<User> technicians = userDAO.findByRole(User.ROLE_TECHNICIAN);

            request.setAttribute("complaints", complaints);
            request.setAttribute("categories", categories);
            request.setAttribute("technicians", technicians);

            // Flash notifications
            if ("true".equals(request.getParameter("assigned"))) {
                request.setAttribute("successMessage", "Technician assigned successfully! Audit record logged.");
            }

            // Retain filter state in UI
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedStatus", status != null ? status.toUpperCase() : "ALL");
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("selectedPriority", priority != null ? priority.toUpperCase() : "ALL");

            request.getRequestDispatcher("/WEB-INF/views/admin/complaints.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to execute complaint search and filtering", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading complaints.");
        }
    }

    private void handleAssignTechnician(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {

        HttpSession session = request.getSession(false);
        int adminId = (Integer) session.getAttribute("userId");

        String complaintIdStr = request.getParameter("complaintId");
        String technicianIdStr = request.getParameter("technicianId");
        String remarks = request.getParameter("remarks");

        try {
            int complaintId = Integer.parseInt(complaintIdStr);
            int technicianId = Integer.parseInt(technicianIdStr);

            boolean success = complaintService.assignTechnician(complaintId, technicianId, adminId, remarks);
            if (success) {
                LOGGER.info("Technician " + technicianId + " assigned to complaint " + complaintId);
                response.sendRedirect(request.getContextPath() + "/admin/complaints?assigned=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/complaints?error=failed");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error assigning technician: " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/admin/complaints?error=" + e.getMessage());
        }
    }

    private void handleUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            List<User> allUsers = userDAO.findAll();
            request.setAttribute("users", allUsers);
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load user directory for admin", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load user directory.");
        }
    }
}
