package com.smartclass.service;

import com.smartclass.model.User;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.http.HttpSession;

public class AuthService {

    private com.smartclass.dao.UserDAO userDAO;

    public AuthService() {
        // No-arg constructor required by Tomcat/Filters created via reflection
        // UserDAO will be injected by ServletContextListener during app startup
        this.userDAO = null;
    }

    public AuthService(com.smartclass.dao.UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String email, String password, HttpSession session) {
        if (userDAO == null) {
            return null;
        }
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        if (!user.isActive()) {
            return null;
        }

        // Set user in session
        if (session != null) {
            session.setAttribute("user", user);
        }

        return user;
    }

    public boolean register(User user, String plainPassword) {
        if (userDAO == null) {
            return false;
        }
        // Check if email or college ID already exists
        if (userDAO.existsByEmail(user.getEmail())) {
            return false;
        }
        if (userDAO.existsByCollegeId(user.getCollegeId())) {
            return false;
        }

        // Hash password with BCrypt
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);
        user.setActive(true);

        // Set default role if not set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }

        userDAO.create(user);
        return true;
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }

    public boolean isAuthenticated(HttpSession session) {
        if (session == null) {
            return false;
        }
        User user = getCurrentUser(session);
        return user != null && user.getRole() != null;
    }

    public boolean hasRole(HttpSession session, String role) {
        if (session == null) {
            return false;
        }
        User user = getCurrentUser(session);
        return user != null && role.equals(user.getRole());
    }}
