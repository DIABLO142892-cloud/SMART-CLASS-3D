package com.smartclass.controller;

import com.smartclass.model.User;
import com.smartclass.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class LoginServlet extends HttpServlet {

    private AuthService authService;

    public LoginServlet() {
        // No-arg constructor required by Servlet container
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // AuthService can be obtained from servlet context or Spring context
        // For this pure Jakarta EE app, we'll look it up from context
        AuthService as = (AuthService) config.getServletContext().getAttribute("authService");
        if (as != null) {
            this.authService = as;
        }
    }

@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle session fixation: regenerate session ID before checking auth
        HttpSession session = request.getSession(true);
        request.changeSessionId();

        if (session != null && authService.isAuthenticated(session)) {
            // Already logged in, redirect to appropriate dashboard
            User user = authService.getCurrentUser(session);
            if (user != null) {
                String role = user.getRole();
                if ("ADMIN".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if ("LECTURER".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/student/dashboard");
                }
            }
            return;
        }

        // Show login page
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle session fixation: regenerate session ID before processing login
        HttpSession session = request.getSession(true);
        String newSessionId = request.changeSessionId();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = authService.authenticate(email, password, session);
        if (user != null) {
            // Log login action

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if ("LECTURER".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            }
        } else {
            // Login failed
            request.setAttribute("error", "Invalid email or password. Please try again.");
            request.setAttribute("showDemoInfo", true);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}