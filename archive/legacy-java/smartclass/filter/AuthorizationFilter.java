package com.smartclass.filter;

import com.smartclass.model.User;
import com.smartclass.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class AuthorizationFilter implements Filter {

    private AuthService authService;

    public AuthorizationFilter() {
        // No-arg constructor required by Tomcat/Filters created via reflection
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.authService = (AuthService) filterConfig.getServletContext().getAttribute("authService");
        if (this.authService == null) {
            throw new ServletException("AuthService not initialized. "
                    + "Ensure ServletContextListener has populated 'authService' in context init.");
        }
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);

        User user = authService.getCurrentUser(session);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String requestedPath = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Define role-based access
        boolean allowAccess = true;

        // Admin-only paths
        if (requestedPath.startsWith(contextPath + "/admin/")
                || requestedPath.startsWith(contextPath + "/api/admin/")) {
            allowAccess = "ADMIN".equals(user.getRole());
        }
        // Lecturer-only paths
        else if (requestedPath.startsWith(contextPath + "/lecturer/")
                || requestedPath.startsWith(contextPath + "/api/lecturer/")) {
            allowAccess = "LECTURER".equals(user.getRole()) || "ADMIN".equals(user.getRole());
        }
        // Student paths (accessible to all authenticated users)
        else if (requestedPath.startsWith(contextPath + "/student/")
                || requestedPath.startsWith(contextPath + "/api/student/")) {
            allowAccess = true; // All authenticated users
        }

        if (!allowAccess) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Insufficient privileges.");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}