package com.smartclass.filter;

import com.smartclass.model.User;
import com.smartclass.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class AuthenticationFilter implements Filter {

    private AuthService authService;

    public AuthenticationFilter() {
        // No-arg constructor required by Tomcat/Filters created via reflection
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // AuthService is stored by ServletContextListener during app startup
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

        String requestedPath = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Public paths that don't require authentication
        boolean publicPath = requestedPath.startsWith(contextPath + "/login")
                || requestedPath.startsWith(contextPath + "/css/")
                || requestedPath.startsWith(contextPath + "/js/")
                || requestedPath.startsWith(contextPath + "/images/")
                || requestedPath.startsWith(contextPath + "/fonts/")
                || requestedPath.equals(contextPath + "/")
                || requestedPath.equals(contextPath + "/index");

        if (!publicPath) {
            User user = authService.getCurrentUser(session);
            if (user == null) {
                response.sendRedirect(contextPath + "/login");
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}