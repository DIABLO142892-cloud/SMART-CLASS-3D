package com.smartclass.dao;

import com.smartclass.model.Department;
import java.util.List;

public interface DepartmentDAO {
    List<Department> findAll();
    Department findById(int id);
    Department findByCode(String code);
    Department create(Department department);
    boolean update(Department department);
    boolean delete(int id);
}