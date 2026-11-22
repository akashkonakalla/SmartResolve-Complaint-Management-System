package com.smartresolve.controller;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * UserComplaintServlet handles listing a user's submitted complaints
 * and viewing detailed individual complaint records with IDOR protection.
 */
@WebServlet(name = "UserComplaintServlet", urlPatterns = {"/user/complaints", "/user/complaint/details"})
public class UserComplaintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserComplaintServlet.class.getName());

    private ComplaintService complaintService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintService = new ComplaintService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login?sessionExpired=true");
            return;
        }

        int currentUserId = (Integer) session.getAttribute("userId");
        String currentUserRole = (String) session.getAttribute("userRole");
        String servletPath = request.getServletPath();

        if ("/user/complaints".equals(servletPath)) {
            handleListComplaints(request, response, currentUserId);
        } else if ("/user/complaint/details".equals(servletPath)) {
            handleComplaintDetails(request, response, currentUserId, currentUserRole);
        }
    }

    private void handleListComplaints(HttpServletRequest request, HttpServletResponse response, int userId) 
            throws ServletException, IOException {

        try {
            List<Complaint> complaints = complaintService.getUserComplaints(userId);

            // Optional status filtering (?status=NEW, ?status=IN_PROGRESS, etc.)
            String filterStatus = request.getParameter("status");
            if (filterStatus != null && !filterStatus.trim().isEmpty() && !"ALL".equalsIgnoreCase(filterStatus)) {
                complaints = complaints.stream()
                        .filter(c -> filterStatus.equalsIgnoreCase(c.getStatus()))
                        .collect(Collectors.toList());
                request.setAttribute("selectedStatus", filterStatus.toUpperCase());
            } else {
                request.setAttribute("selectedStatus", "ALL");
            }

            // Notification banners
            if ("true".equals(request.getParameter("submitted"))) {
                request.setAttribute("successMessage", "Service request submitted successfully! We are reviewing your issue.");
            }

            request.setAttribute("complaints", complaints);
            request.getRequestDispatcher("/WEB-INF/views/user/my-complaints.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading complaints for user: " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load complaints at this time.");
        }
    }

    private void handleComplaintDetails(HttpServletRequest request, HttpServletResponse response, 
                                       int currentUserId, String currentUserRole) 
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/user/complaints");
            return;
        }

        int complaintId;
        try {
            complaintId = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid complaint identifier.");
            return;
        }

        try {
            Complaint complaint = complaintService.getComplaintById(complaintId);
            if (complaint == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Complaint record not found.");
                return;
            }

            // IDOR Protection: Ensure user can ONLY access their own complaints (unless administrator)
            if (complaint.getUserId() != currentUserId && !User.ROLE_ADMIN.equalsIgnoreCase(currentUserRole)) {
                LOGGER.warning("Unauthorized access attempt: User " + currentUserId + " attempted to view complaint " + complaintId);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You are not authorized to view this complaint.");
                return;
            }

            request.setAttribute("complaint", complaint);
            request.getRequestDispatcher("/WEB-INF/views/user/complaint-details.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading complaint details for ID: " + complaintId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve complaint details.");
        }
    }
}
