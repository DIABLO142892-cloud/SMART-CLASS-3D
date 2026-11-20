package com.smartclass.controller;

import com.smartclass.dto.DashboardSummaryDTO;
import com.smartclass.model.User;
import com.smartclass.service.StatusCalculationService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;

public class AdminDashboardServlet extends HttpServlet {

    private StatusCalculationService statusService;

    public AdminDashboardServlet() {
        // No-arg constructor required by Servlet container
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        StatusCalculationService scs = (StatusCalculationService) config.getServletContext().getAttribute("statusService");
        if (scs != null) {
            this.statusService = scs;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        summary.setTotalClassrooms(50);
        summary.setAvailable(30);
        summary.setOccupied(15);
        summary.setReserved(3);
        summary.setMaintenance(2);
        summary.setUtilizationPercentage(60.0);
        summary.setCurrentTime(LocalDateTime.now());

        request.setAttribute("user", user);
        request.setAttribute("summary", summary);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("buildings".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/buildings");
        } else if ("classrooms".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/classrooms");
        } else if ("users".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else if ("timetable".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/timetable");
        } else if ("bookings".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        } else {
            doGet(request, response);
        }
    }
}