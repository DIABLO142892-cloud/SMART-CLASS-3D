package com.smartclass.dao;

import com.smartclass.model.Building;
import java.util.List;

public interface BuildingDAO {
    List<Building> findAll();
    Building findById(int id);
    List<Building> findByFloor(int floorNumber);
    Building create(Building building);
    boolean update(Building building);
    boolean delete(int id);
}