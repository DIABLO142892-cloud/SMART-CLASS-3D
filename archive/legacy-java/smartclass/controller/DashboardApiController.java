package com.smartclass.controller;

import com.smartclass.dto.DashboardSummaryDTO;
import com.smartclass.dto.ClassroomDTO;
import com.smartclass.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class DashboardApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String endpoint = request.getPathInfo();

        if (endpoint == null || endpoint.equals("/")) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);

            DashboardSummaryDTO summary = new DashboardSummaryDTO();
            summary.setTotalClassrooms(50);
            summary.setAvailable(30);
            summary.setOccupied(15);
            summary.setReserved(3);
            summary.setMaintenance(2);
            summary.setUtilizationPercentage(60.0);
            summary.setCurrentTime(java.time.LocalDateTime.now());

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            try {
                mapper.writeValue(response.getOutputStream(), summary);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating response");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}