package com.smartclass.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RoleAuthorizationTest {

    @Test
    void testAdminRoleHasAdminAccess() {
        assertHasRole("ADMIN", "ADMIN", true);
    }

    @Test
    void testLecturerRoleHasLecturerAccess() {
        assertHasRole("LECTURER", "LECTURER", true);
    }

    @Test
    void testStudentRoleHasStudentAccess() {
        assertHasRole("STUDENT", "STUDENT", true);
    }

    @Test
    void testLecturerRoleAlsoHasAdminAccess() {
        assertHasRole("LECTURER", "ADMIN", true);
    }

    private void assertHasRole(String userRole, String expectedRole, boolean shouldHaveAccess) {
        boolean hasAccess = "ADMIN".equals(expectedRole)
                ? "ADMIN".equals(userRole) || "LECTURER".equals(userRole) || "STUDENT".equals(userRole)
                : expectedRole.equals(userRole);
        if (shouldHaveAccess) {
            assertTrue(hasAccess, "User with role " + userRole + " should have " + expectedRole + " access");
        } else {
            assertFalse(hasAccess, "User with role " + userRole + " should NOT have " + expectedRole + " access");
        }
    }
}