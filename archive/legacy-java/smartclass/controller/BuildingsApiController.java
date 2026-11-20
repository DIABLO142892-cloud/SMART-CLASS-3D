package com.smartclass.controller;

import com.smartclass.model.Building;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class BuildingsApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        List<Building> buildings = new ArrayList<>();

        // Sample buildings
        Building b1 = new Building();
        b1.setId(1);
        b1.setCode("BLOCK-A");
        b1.setName("Main Academic Building");
        b1.setDescription("Main building with lecture halls");
        b1.setTotalFloors(4);
        b1.setCoordinateX(0);
        b1.setCoordinateY(0);
        b1.setCoordinateZ(0);
        b1.setActive(true);

        Building b2 = new Building();
        b2.setId(2);
        b2.setCode("BLOCK-B");
        b2.setName("Science Building");
        b2.setDescription("Science and laboratories");
        b2.setTotalFloors(3);
        b2.setCoordinateX(100);
        b2.setCoordinateY(0);
        b2.setCoordinateZ(0);
        b2.setActive(true);

        Building b3 = new Building();
        b3.setId(3);
        b3.setCode("BLOCK-C");
        b3.setName("Student Center");
        b3.setDescription("Student services and common areas");
        b3.setTotalFloors(2);
        b3.setCoordinateX(200);
        b3.setCoordinateY(0);
        b3.setCoordinateZ(0);
        b3.setActive(true);

        buildings.add(b1);
        buildings.add(b2);
        buildings.add(b3);

        try {
            mapper.writeValue(response.getOutputStream(), buildings);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating response");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}