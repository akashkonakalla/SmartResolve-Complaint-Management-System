package com.smartresolve.controller;

import com.smartresolve.model.Category;
import com.smartresolve.model.Complaint;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ComplaintServlet orchestrates complaint submission and user service requests.
 */
@WebServlet(name = "ComplaintServlet", urlPatterns = {"/user/complaint/create", "/complaint/create"})
public class ComplaintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ComplaintServlet.class.getName());

    private ComplaintService complaintService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintService = new ComplaintService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            List<Category> categories = complaintService.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/user/create-complaint.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load categories for complaint form", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load complaint form. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?sessionExpired=true");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        String title = request.getParameter("title");
        String categoryIdStr = request.getParameter("categoryId");
        String location = request.getParameter("location");
        String priority = request.getParameter("priority");
        String description = request.getParameter("description");

        int categoryId = 0;
        try {
            if (categoryIdStr != null) {
                categoryId = Integer.parseInt(categoryIdStr.trim());
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            Complaint created = complaintService.submitComplaint(userId, categoryId, title, description, location, priority, null);
            LOGGER.info("Complaint submitted: " + created.getComplaintNumber() + " by User ID: " + userId);

            // Post/Redirect/Get pattern
            response.sendRedirect(request.getContextPath() + "/user/complaints?submitted=true");

        } catch (IllegalArgumentException e) {
            // Form validation error
            try {
                request.setAttribute("categories", complaintService.getAllCategories());
            } catch (SQLException ignored) {
            }
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("title", title);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("location", location);
            request.setAttribute("selectedPriority", priority);
            request.setAttribute("description", description);
            request.getRequestDispatcher("/WEB-INF/views/user/create-complaint.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during complaint submission", e);
            try {
                request.setAttribute("categories", complaintService.getAllCategories());
            } catch (SQLException ignored) {
            }
            request.setAttribute("errorMessage", "A database error occurred. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/user/create-complaint.jsp").forward(request, response);
        }
    }
}
