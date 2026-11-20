package com.smartclass.controller;

import com.smartclass.dto.BookingDTO;
import com.smartclass.model.Booking;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.*;

public class BookingsApiController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        
        try {
            // Parse booking request
            String classroomIdStr = request.getParameter("classroomId");
            String purpose = request.getParameter("purpose");
            String startStr = request.getParameter("startDateTime");
            String endStr = request.getParameter("endDateTime");

            if (classroomIdStr == null || purpose == null || startStr == null || endStr == null) {
                mapper.writeValue(response.getOutputStream(), 
                    com.smartclass.dto.JsonResponse.error("Missing required parameters"));
                return;
            }

            int classroomId = Integer.parseInt(classroomIdStr);
            LocalDateTime start = LocalDateTime.parse(startStr);
            LocalDateTime end = LocalDateTime.parse(endStr);

            // Check for overlapping bookings
            // Would use BookingDAO.hasOverlappingBooking(classroomId, start, end)

            // Create booking
            Booking booking = new Booking();
            booking.setClassroomId(classroomId);
            booking.setPurpose(purpose);
            booking.setStartDateTime(start);
            booking.setEndDateTime(end);
            booking.setStatus("PENDING");

            // Would use BookingDAO.create(booking)

            mapper.writeValue(response.getOutputStream(),
                com.smartclass.dto.JsonResponse.success("Booking request submitted successfully, pending approval"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating booking: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}