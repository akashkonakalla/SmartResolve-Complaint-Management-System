package com.smartresolve.controller;

import com.smartresolve.dao.ComplaintDAO;
import com.smartresolve.dao.UserDAO;
import com.smartresolve.model.Complaint;
import com.smartresolve.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AdminServlet orchestrates the administrator management portal,
 * system-wide complaint oversight, and technician workload monitoring.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/dashboard", "/admin/users"})
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminServlet.class.getName());

    private ComplaintDAO complaintDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintDAO = new ComplaintDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/dashboard".equals(path)) {
            handleDashboard(request, response);
        } else if ("/admin/users".equals(path)) {
            handleUsers(request, response);
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
