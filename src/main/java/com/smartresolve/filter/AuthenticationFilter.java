package com.smartresolve.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * AuthenticationFilter intercepts incoming HTTP requests to ensure unauthenticated
 * users cannot access protected resources.
 */
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    // Publicly accessible paths that do not require an active session
    private static final Set<String> PUBLIC_EXACT_PATHS = new HashSet<>(Arrays.asList(
        "",
        "/",
        "/index.jsp",
        "/login",
        "/register",
        "/logout"
    ));

    private static final String[] PUBLIC_PREFIXES = {
        "/assets/",
        "/css/",
        "/js/",
        "/images/",
        "/favicon.ico"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        String relativePath = uri.substring(contextPath.length());

        // Check if the requested path is public
        if (isPublicResource(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        // Verify active authenticated session
        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = (session != null && session.getAttribute("userId") != null);

        if (isAuthenticated) {
            // User is signed in — proceed through the filter chain
            chain.doFilter(request, response);
        } else {
            // Unauthenticated request to a protected endpoint — redirect to login
            LOGGER.fine("Unauthenticated access attempt to: " + relativePath + " — redirecting to login.");
            httpResponse.sendRedirect(contextPath + "/login?sessionExpired=true");
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

    private boolean isPublicResource(String path) {
        if (PUBLIC_EXACT_PATHS.contains(path)) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
