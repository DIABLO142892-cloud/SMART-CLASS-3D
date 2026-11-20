package com.smartclass.controller;

import com.smartclass.dto.ClassroomDTO;
import com.smartclass.model.Classroom;
import com.smartclass.model.Building;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class ClassroomsApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String statusFilter = request.getParameter("status");
        String buildingIdStr = request.getParameter("building");
        String floorStr = request.getParameter("floor");
        String roomType = request.getParameter("roomType");
        String capacityMinStr = request.getParameter("capacityMin");

        response.setContentType("application/json");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        List<ClassroomDTO> results = new ArrayList<>();

        // Simulated data - in real implementation would use ClassroomDAO
        if ("list".equals(action) || action == null) {
            int buildingId = buildingIdStr != null ? Integer.parseInt(buildingIdStr) : 0;
            int floor = floorStr != null ? Integer.parseInt(floorStr) : 0;
            int capacityMin = capacityMinStr != null ? Integer.parseInt(capacityMinStr) : 0;

            // Create sample classrooms with status
            for (int i = 1; i <= 20; i++) {
                ClassroomDTO dto = new ClassroomDTO();
                Classroom classroom = new Classroom();
                classroom.setId(i);
                classroom.setRoomNumber("A-" + String.format("%03d", i));
                classroom.setRoomName("Classroom " + i);
                classroom.setCapacity(30 + (i * 5) % 50);
                classroom.setRoomType(i <= 5 ? "Lecture Hall" : i <= 10 ? "Seminar Hall" : i <= 15 ? "Computer Lab" : "Tutorial Room");
                classroom.setActive(true);

                dto.setClassroom(classroom);
                dto.setBuilding(new Building());
                dto.getBuilding().setId(buildingId > 0 ? buildingId : (i <= 5 ? 1 : (i <= 10 ? 2 : 3)));
                dto.getBuilding().setName("Building " + (i <= 5 ? "A" : (i <= 10 ? "B" : "C")));
                dto.getBuilding().setCode("BLOCK-" + (i <= 5 ? "A" : (i <= 10 ? "B" : "C")));

                // Calculate simple availability status
                dto.setAvailabilityStatus("AVAILABLE");
                dto.setAvailabilityUntil("--");

                results.add(dto);
            }
        }

        try {
            mapper.writeValue(response.getOutputStream(), results);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating response");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}