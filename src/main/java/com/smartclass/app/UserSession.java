package com.smartclass.app;

import java.io.Serializable;

public record UserSession(int id, String collegeId, String fullName, String email,
                          String role, String department) implements Serializable {
    // JSP EL resolves JavaBean getters; expose them in addition to record accessors.
    public int getId() { return id; }
    public String getCollegeId() { return collegeId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }

    public boolean hasRole(String expected) {
        return role != null && role.equalsIgnoreCase(expected);
    }
}
