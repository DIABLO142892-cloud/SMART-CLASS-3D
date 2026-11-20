package com.smartclass.dao;

import com.smartclass.model.User;
import java.util.*;

public class UserDAOImpl implements UserDAO {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, User> byEmail = new HashMap<>();
    private int nextId = 1;

    public UserDAOImpl() {
        // Seed some default users
        User admin = new User();
        admin.setId(1);
        admin.setCollegeId("ADM001");
        admin.setFullName("Administrator");
        admin.setEmail("admin@smartclass.com");
        admin.setPasswordHash("$2a$10$1OPOCCoYVbGN/yHtp/rGceDTEw19rubu849gX8XzOveVv/TfWA6kK");
        admin.setRole("ADMIN");
        admin.setActive(true);
        users.put("ADM001", admin);
        byEmail.put("admin@smartclass.com", admin);

        User lecturer1 = new User();
        lecturer1.setId(2);
        lecturer1.setCollegeId("LEC001");
        lecturer1.setFullName("Dr. Smith");
        lecturer1.setEmail("lecturer@smartclass.com");
        lecturer1.setPasswordHash("$2a$10$dFIjfFyAtrR3fi.D/6LA0ewdpaJ1tH7jr1KczeZRJGiXe34Xh3wEq");
        lecturer1.setRole("LECTURER");
        lecturer1.setActive(true);
        users.put("LEC001", lecturer1);
        byEmail.put("lecturer@smartclass.com", lecturer1);

        User student1 = new User();
        student1.setId(3);
        student1.setCollegeId("STU001");
        student1.setFullName("John Doe");
        student1.setEmail("student@smartclass.com");
        student1.setPasswordHash("$2a$10$DOQHRYwoyDk9IhpubFK9uunS2zBJ7swPIWKT3zKtZcoIQXANjnAnG");
        student1.setRole("STUDENT");
        student1.setActive(true);
        users.put("STU001", student1);
        byEmail.put("student@smartclass.com", student1);

        User student2 = new User();
        student2.setId(4);
        student2.setCollegeId("STU002");
        student2.setFullName("Jane Smith");
        student2.setEmail("jstudent@smartclass.com");
        student2.setPasswordHash("$2a$10$DOQHRYwoyDk9IhpubFK9uunS2zBJ7swPIWKT3zKtZcoIQXANjnAnG");
        student2.setRole("STUDENT");
        student2.setActive(true);
        users.put("STU002", student2);
        byEmail.put("jstudent@smartclass.com", student2);

        User lecturer2 = new User();
        lecturer2.setId(5);
        lecturer2.setCollegeId("LEC002");
        lecturer2.setFullName("Dr. Johnson");
        lecturer2.setEmail("lecturer2@smartclass.com");
        lecturer2.setPasswordHash("$2a$10$dFIjfFyAtrR3fi.D/6LA0ewdpaJ1tH7jr1KczeZRJGiXe34Xh3wEq");
        lecturer2.setRole("LECTURER");
        lecturer2.setActive(true);
        users.put("LEC002", lecturer2);
        byEmail.put("lecturer2@smartclass.com", lecturer2);
    }

    @Override
    public User findByEmail(String email) {
        return byEmail.get(email);
    }

    @Override
    public User findByCollegeId(String collegeId) {
        return users.get(collegeId);
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User u : users.values()) {
            if (role.equals(u.getRole())) {
                result.add(u);
            }
        }
        return result;
    }

    @Override
    public User create(User user) {
        users.put(user.getCollegeId(), user);
        byEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public boolean update(User user) {
        if (users.containsKey(user.getCollegeId())) {
            users.put(user.getCollegeId(), user);
            byEmail.put(user.getEmail(), user);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email);
    }

    @Override
    public boolean existsByCollegeId(String collegeId) {
        return users.containsKey(collegeId);
    }
}