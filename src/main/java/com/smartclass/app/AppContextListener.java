package com.smartclass.app;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppContextListener implements ServletContextListener {
    private AnnotationConfigApplicationContext spring;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        spring = new AnnotationConfigApplicationContext(AppConfig.class);
        event.getServletContext().setAttribute("springContext", spring);
        event.getServletContext().setAttribute("smartClassService", spring.getBean(SmartClassService.class));
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (spring != null) spring.close();
    }
}
