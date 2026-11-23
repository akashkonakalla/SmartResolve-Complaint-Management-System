package com.smartresolve.controller;

import com.smartresolve.dao.ComplaintDAO;
import com.smartresolve.model.Complaint;
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
 * DashboardServlet renders the authenticated resident/user dashboard,
 * displaying real-time metrics, quick action shortcuts, and recent activity.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/user/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private ComplaintDAO complaintDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintDAO = new ComplaintDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?sessionExpired=true");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        try {
            // Retrieve live aggregated statistics for this user
            Map<String, Integer> stats = complaintDAO.getUserStatistics(userId);
            List<Complaint> recentComplaints = complaintDAO.findRecentByUserId(userId, 5);

            request.setAttribute("stats", stats);
            request.setAttribute("recentComplaints", recentComplaints);

            request.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load dashboard metrics for user: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load dashboard.");
        }
    }
}
