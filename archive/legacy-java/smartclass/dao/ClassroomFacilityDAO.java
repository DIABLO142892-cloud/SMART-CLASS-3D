package com.smartclass.dao;

import com.smartclass.model.ClassroomFacility;
import java.util.List;

public interface ClassroomFacilityDAO {
    List<ClassroomFacility> findByClassroomId(int classroomId);
    ClassroomFacility findByClassroomAndFacility(int classroomId, int facilityId);
    boolean create(ClassroomFacility cf);
    boolean update(ClassroomFacility cf);
    boolean deleteByClassroomId(int classroomId);
}