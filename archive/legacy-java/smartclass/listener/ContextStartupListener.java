package com.smartclass.listener;

import com.smartclass.dao.UserDAOImpl;
import com.smartclass.service.AuthService;
import jakarta.servlet.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextStartupListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ContextStartupListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx =sce.getServletContext();

        // Initialize UserDAO
        UserDAOImpl userDAO = new UserDAOImpl();

        // Initialize AuthService and store in ServletContext
        AuthService authService = new AuthService(userDAO);
        ctx.setAttribute("authService", authService);
        logger.info("AuthService initialized and stored in ServletContext");

        logger.info("SmartClass 3D application startup complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext ctx =sce.getServletContext();
        logger.info("SmartClass 3D application shutdown");
    }
}