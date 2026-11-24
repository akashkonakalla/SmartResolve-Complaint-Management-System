package com.smartresolve.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * CsrfFilter provides cross-site request forgery protection by enforcing
 * cryptographically secure anti-CSRF synchronizer tokens on state-changing requests.
 */
public class CsrfFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(CsrfFilter.class.getName());
    public static final String CSRF_SESSION_ATTR = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "csrfToken";

    private final SecureRandom secureRandom = new SecureRandom();

    // Endpoints exempted from strict CSRF enforcement (e.g. initial login/registration)
    private static final Set<String> EXEMPT_PATHS = new HashSet<>();

    static {
        EXEMPT_PATHS.add("/login");
        EXEMPT_PATHS.add("/register");
        EXEMPT_PATHS.add("/logout");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("CsrfFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getDispatcherType() == DispatcherType.ERROR || 
            httpRequest.getDispatcherType() == DispatcherType.FORWARD) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(true);
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);

        if (sessionToken == null) {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            sessionToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            session.setAttribute(CSRF_SESSION_ATTR, sessionToken);
        }

        // Expose token to request scope for JSP rendering
        httpRequest.setAttribute("csrfToken", sessionToken);

        String method = httpRequest.getMethod();
        String path = httpRequest.getServletPath();

        // Enforce CSRF check on state-changing HTTP requests
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            if (!EXEMPT_PATHS.contains(path)) {
                String requestToken = httpRequest.getParameter(CSRF_PARAM_NAME);
                if (requestToken == null || requestToken.trim().isEmpty()) {
                    requestToken = httpRequest.getHeader("X-CSRF-TOKEN");
                }

                if (requestToken == null || !sessionToken.equals(requestToken)) {
                    LOGGER.warning("CSRF validation failure on path: " + path + " from IP: " + httpRequest.getRemoteAddr());
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Security validation failed: Invalid or missing CSRF token.");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
