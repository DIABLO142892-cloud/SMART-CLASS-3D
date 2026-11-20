package com.smartclass.dao;

import com.smartclass.model.Classroom;
import java.util.List;

public interface ClassroomDAO {
    List<Classroom> findAll();
    Classroom findById(int id);
    List<Classroom> findByBuildingId(int buildingId);
    List<Classroom> findByDepartmentId(int departmentId);
    List<Classroom> findByStatus(String status);
    List<Classroom> search(Classroom searchCriteria);
    boolean create(Classroom classroom);
    boolean update(Classroom classroom);
    boolean delete(int id);
}