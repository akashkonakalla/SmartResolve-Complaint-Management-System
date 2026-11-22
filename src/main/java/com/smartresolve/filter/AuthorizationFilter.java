package com.smartresolve.filter;

import com.smartresolve.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * AuthorizationFilter enforces role-based access control (RBAC).
 * Ensures that authenticated users only access endpoints authorized for their specific role.
 */
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthorizationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        String relativePath = uri.substring(contextPath.length());

        HttpSession session = httpRequest.getSession(false);

        // If no session exists, AuthenticationFilter should handle it, but guard defensively
        if (session == null || session.getAttribute("userId") == null) {
            chain.doFilter(request, response);
            return;
        }

        String userRole = (String) session.getAttribute("userRole");

        // Role-based path authorization rules
        if (relativePath.startsWith("/admin/")) {
            if (!User.ROLE_ADMIN.equalsIgnoreCase(userRole)) {
                LOGGER.warning("Access denied: User " + session.getAttribute("userEmail") + 
                               " [Role: " + userRole + "] tried to access admin path: " + relativePath);
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
                return;
            }
        } else if (relativePath.startsWith("/technician/")) {
            if (!User.ROLE_TECHNICIAN.equalsIgnoreCase(userRole) && !User.ROLE_ADMIN.equalsIgnoreCase(userRole)) {
                LOGGER.warning("Access denied: User " + session.getAttribute("userEmail") + 
                               " [Role: " + userRole + "] tried to access technician path: " + relativePath);
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Technician role required.");
                return;
            }
        } else if (relativePath.startsWith("/user/")) {
            if (!User.ROLE_USER.equalsIgnoreCase(userRole) && !User.ROLE_ADMIN.equalsIgnoreCase(userRole)) {
                LOGGER.warning("Access denied: User " + session.getAttribute("userEmail") + 
                               " [Role: " + userRole + "] tried to access resident/user path: " + relativePath);
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: User role required.");
                return;
            }
        }

        // Access authorized
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
