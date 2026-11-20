package com.smartclass.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {
    private final UserSession student=new UserSession(3,"STU001","John Doe","student@smartclass.com","STUDENT","CSE");
    @Test void hasRoleIsCaseInsensitive(){ assertTrue(student.hasRole("student")); }
    @Test void differentRoleDoesNotMatch(){ assertFalse(student.hasRole("ADMIN")); }
    @Test void recordKeepsIdentity(){ assertAll(()->assertEquals(3,student.id()),()->assertEquals("STU001",student.collegeId()),()->assertEquals("John Doe",student.fullName()),()->assertEquals("CSE",student.department())); }
}
