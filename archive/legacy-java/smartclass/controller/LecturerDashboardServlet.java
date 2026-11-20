package com.smartclass.controller;

import com.smartclass.dto.DashboardSummaryDTO;
import com.smartclass.model.User;
import com.smartclass.service.StatusCalculationService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.LocalDateTime;

public class LecturerDashboardServlet extends HttpServlet {

    public LecturerDashboardServlet() {
        // No-arg constructor required by Servlet container
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
        summary.setUtilizationPercentage(60.0);
        summary.setCurrentTime(LocalDateTime.now());

        request.setAttribute("user", user);
        request.setAttribute("summary", summary);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/lecturer_dashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("occupy".equals(action)) {
            int classroomId = Integer.parseInt(request.getParameter("classroomId"));
            // Would call statusService.occupyClassroom(classroomId);
            response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
        } else if ("release".equals(action)) {
            int classroomId = Integer.parseInt(request.getParameter("classroomId"));
            // Would call statusService.releaseClassroom(classroomId);
            response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
        } else if ("cancel".equals(action)) {
            int classroomId = Integer.parseInt(request.getParameter("classroomId"));
            // Would call statusService.cancelClass(classroomId);
            response.sendRedirect(request.getContextPath() + "/lecturer/dashboard");
        } else {
            doGet(request, response);
        }
    }
}