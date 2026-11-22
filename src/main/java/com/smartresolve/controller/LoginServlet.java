package com.smartresolve.controller;

import com.smartresolve.model.User;
import com.smartresolve.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoginServlet manages user sign-in requests, credential validation,
 * session rotation (fixation protection), and role-based redirects.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // If user already has an active authenticated session, redirect to their dashboard
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("userId") != null) {
            String role = (String) existingSession.getAttribute("userRole");
            response.sendRedirect(request.getContextPath() + authService.getDashboardRedirectPath(role));
            return;
        }

        // Handle flash status parameters
        if ("true".equals(request.getParameter("registered"))) {
            request.setAttribute("successMessage", "Account created successfully! Please sign in with your credentials.");
        } else if ("true".equals(request.getParameter("logout"))) {
            request.setAttribute("infoMessage", "You have been logged out successfully.");
        } else if ("true".equals(request.getParameter("sessionExpired"))) {
            request.setAttribute("warningMessage", "Your session has expired. Please log in again.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authService.authenticate(email, password);
            LOGGER.info("User logged in successfully: " + user.getEmail() + " [Role: " + user.getRole() + "]");

            // Session Fixation Defense: Invalidate old session and create a fresh one
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create new secure session
            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(1800); // 30 minutes timeout

            // Bind essential identity attributes
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userName", user.getFullName());
            newSession.setAttribute("userEmail", user.getEmail());
            newSession.setAttribute("userRole", user.getRole());
            newSession.setAttribute("currentUser", user);

            // Post/Redirect/Get pattern: redirect to appropriate role-specific dashboard
            String targetPath = authService.getDashboardRedirectPath(user.getRole());
            response.sendRedirect(request.getContextPath() + targetPath);

        } catch (IllegalArgumentException e) {
            // Invalid credentials
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failure during login for: " + email, e);
            request.setAttribute("errorMessage", "Unable to connect to service. Please try again shortly.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
