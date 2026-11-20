package com.smartclass.dao;

import com.smartclass.model.User;
import java.util.List;

public interface UserDAO {
    User findByEmail(String email);
    User findByCollegeId(String collegeId);
    List<User> findByRole(String role);
    User create(User user);
    boolean update(User user);
    boolean delete(int id);
    boolean existsByEmail(String email);
    boolean existsByCollegeId(String collegeId);
}