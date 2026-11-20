package com.smartclass.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

public class DashboardServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        request.setAttribute("user",WebSupport.user(request));
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request,response);
    }
}
