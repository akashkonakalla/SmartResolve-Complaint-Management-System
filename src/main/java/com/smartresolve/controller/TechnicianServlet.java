package com.smartresolve.controller;

import com.smartresolve.dao.ComplaintDAO;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TechnicianServlet provides maintenance staff with dedicated portals for monitoring
 * assigned service tasks and executing status transitions (Start Work, Resolve).
 */
@WebServlet(name = "TechnicianServlet", urlPatterns = {"/technician/dashboard", "/technician/complaints", "/technician/status/update"})
public class TechnicianServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(TechnicianServlet.class.getName());

    private ComplaintDAO complaintDAO;
    private ComplaintService complaintService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.complaintDAO = new ComplaintDAO();
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

        int technicianId = (Integer) session.getAttribute("userId");
        String path = request.getServletPath();

        if ("/technician/dashboard".equals(path)) {
            handleDashboard(request, response, technicianId);
        } else if ("/technician/complaints".equals(path)) {
            handleAssignedComplaints(request, response, technicianId);
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

        int technicianId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        String complaintIdStr = request.getParameter("complaintId");
        String targetStatus = request.getParameter("status");
        String remarks = request.getParameter("remarks");

        try {
            int complaintId = Integer.parseInt(complaintIdStr);
            boolean updated = complaintService.updateComplaintStatus(complaintId, targetStatus, technicianId, userRole, remarks);

            if (updated) {
                LOGGER.info("Technician " + technicianId + " updated complaint " + complaintId + " to " + targetStatus);
                response.sendRedirect(request.getContextPath() + "/technician/complaints?updated=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/technician/complaints?error=update_failed");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Validation failure updating complaint: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/technician/complaints?error=" + e.getMessage());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error updating complaint status", e);
            response.sendRedirect(request.getContextPath() + "/technician/complaints?error=database_error");
        }
    }

    private void handleDashboard(HttpServletRequest request, HttpServletResponse response, int technicianId) 
            throws ServletException, IOException {

        try {
            Map<String, Integer> stats = complaintDAO.getTechnicianStatistics(technicianId);
            List<Complaint> assignedComplaints = complaintDAO.findByTechnicianId(technicianId);

            request.setAttribute("stats", stats);
            request.setAttribute("complaints", assignedComplaints);

            request.getRequestDispatcher("/WEB-INF/views/technician/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load technician dashboard data for: " + technicianId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load technician portal.");
        }
    }

    private void handleAssignedComplaints(HttpServletRequest request, HttpServletResponse response, int technicianId) 
            throws ServletException, IOException {

        try {
            List<Complaint> assignedComplaints = complaintDAO.findByTechnicianId(technicianId);
            request.setAttribute("complaints", assignedComplaints);

            // Flash banners
            if ("true".equals(request.getParameter("updated"))) {
                request.setAttribute("successMessage", "Work order status successfully updated and logged in the audit trail.");
            } else if (request.getParameter("error") != null) {
                request.setAttribute("errorMessage", request.getParameter("error"));
            }

            request.getRequestDispatcher("/WEB-INF/views/technician/assigned-complaints.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load assigned complaints for technician: " + technicianId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load assigned tasks.");
        }
    }
}
