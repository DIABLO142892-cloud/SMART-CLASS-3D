package com.smartclass.service;

import com.smartclass.model.Classroom;
import com.smartclass.model.Facility;
import com.smartclass.model.ClassroomFacility;
import com.smartclass.model.Building;
import com.smartclass.dto.ClassroomDTO;
import com.smartclass.dao.ClassroomDAO;
import com.smartclass.dao.FacilityDAO;
import com.smartclass.dao.ClassroomFacilityDAO;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.*;

public class ClassroomService {

    private final ClassroomDAO classroomDAO;
    private final FacilityDAO facilityDAO;
    private final ClassroomFacilityDAO classroomFacilityDAO;

    public ClassroomService(ClassroomDAO classroomDAO,
                            FacilityDAO facilityDAO,
                            ClassroomFacilityDAO classroomFacilityDAO) {
        this.classroomDAO = classroomDAO;
        this.facilityDAO = facilityDAO;
        this.classroomFacilityDAO = classroomFacilityDAO;
    }

    public List<ClassroomDTO> getAllWithDetails() {
        List<Classroom> classrooms = classroomDAO.findAll();
        List<Facility> allFacilities = facilityDAO.findAll();
        List<ClassroomFacility> allCf = new ArrayList<>();

        return classrooms.stream().map(classroom -> {
            ClassroomDTO dto = new ClassroomDTO();
            dto.setClassroom(classroom);

            // Get building info
            Building building = classroom.getBuilding();
            if (building != null) {
                dto.setBuilding(building);
            }

            // Get facilities for this classroom using classroom ID match
            List<Facility> classroomFacilities = new ArrayList<>();
            if (allCf != null) {
                for (ClassroomFacility cf : allCf) {
                    if (cf.getClassroomId() == classroom.getId()) {
                        // Look up facility by ID from the allFacilities list
                        for (Facility f : allFacilities) {
                            if (f.getId() == cf.getFacilityId()) {
                                classroomFacilities.add(f);
                                break;
                            }
                        }
                    }
                }
            }
            dto.setFacilities(classroomFacilities);

            // Calculate availability status (simplified)
            dto.setAvailabilityStatus("AVAILABLE");
            dto.setAvailabilityUntil("--");

            return dto;
        }).collect(Collectors.toList());
    }

    public List<Classroom> search(Classroom searchCriteria) {
        return classroomDAO.search(searchCriteria);
    }

    public Classroom getById(int id) {
        return classroomDAO.findById(id);
    }

    public boolean addFacilityToClassroom(int classroomId, int facilityId, int quantity) {
        if (classroomFacilityDAO.findByClassroomAndFacility(classroomId, facilityId) != null) {
            return false; // Already exists
        }
        ClassroomFacility cf = new ClassroomFacility();
        cf.setClassroomId(classroomId);
        cf.setFacilityId(facilityId);
        cf.setQuantity(quantity);
        cf.setWorking(true);
        return classroomFacilityDAO.create(cf);
    }

    public boolean removeFacilityFromClassroom(int classroomId, int facilityId) {
        return classroomFacilityDAO.deleteByClassroomId(classroomId);
    }

    public List<Facility> getAllFacilities() {
        return facilityDAO.findAll();
    }

    public int getTotalClassrooms() {
        return classroomDAO.findAll().size();
    }

    public int getAvailableCount() {
        int count = 0;
        for (Classroom c : classroomDAO.findAll()) {
            if ("AVAILABLE".equals(c.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getOccupiedCount() {
        int count = 0;
        for (Classroom c : classroomDAO.findAll()) {
            if ("OCCUPIED".equals(c.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getReservedCount() {
        int count = 0;
        for (Classroom c : classroomDAO.findAll()) {
            if ("RESERVED".equals(c.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getUnderMaintenanceCount() {
        int count = 0;
        for (Classroom c : classroomDAO.findAll()) {
            if ("MAINTENANCE".equals(c.getStatus())) {
                count++;
            }
        }
        return count;
    }
}