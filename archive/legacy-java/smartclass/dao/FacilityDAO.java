package com.smartclass.dao;

import com.smartclass.model.Facility;
import java.util.List;

public interface FacilityDAO {
    List<Facility> findAll();
    Facility findById(int id);
    Facility findByName(String name);
    Facility create(Facility facility);
    boolean update(Facility facility);
    boolean delete(int id);
}