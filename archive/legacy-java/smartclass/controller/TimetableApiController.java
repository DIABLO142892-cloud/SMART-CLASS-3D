package com.smartclass.controller;

import com.smartclass.dto.TimetableDTO;
import com.smartclass.model.Timetable;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class TimetableApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String classroomIdStr = request.getParameter("classroomId");
        String day = request.getParameter("day");
        
        response.setContentType("application/json");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        List<TimetableDTO> result = new ArrayList<>();

        int classroomId = classroomIdStr != null ? Integer.parseInt(classroomIdStr) : 0;

        // Sample timetable data
        if (classroomId > 0) {
            for (int i = 1; i <= 5; i++) {
                TimetableDTO dto = new TimetableDTO();
                Timetable t = new Timetable();
                t.setId(i);
                t.setSubjectCode("CS" + i);
                t.setSubjectName("Computer Science " + i);
                t.setSectionName("A");
                t.setDayOfWeek(day != null ? day : "Monday");
                t.setStartTime("09:00");
                t.setEndTime("10:30");
                t.setRoomName("Classroom " + classroomId);
                t.setLecturerName("Dr. Smith");
                t.setStatus("ACTIVE");

                dto.setId(t.getId());
                dto.setSubjectCode(t.getSubjectCode());
                dto.setSubjectName(t.getSubjectName());
                dto.setSectionName(t.getSectionName());
                dto.setDayOfWeek(t.getDayOfWeek());
                dto.setStartTime(t.getStartTime());
                dto.setEndTime(t.getEndTime());
                dto.setRoomName(t.getRoomName());
                dto.setLecturerName(t.getLecturerName());
                dto.setActive("ACTIVE".equals(t.getStatus()));

                result.add(dto);
            }
        }

        try {
            mapper.writeValue(response.getOutputStream(), result);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating response");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}