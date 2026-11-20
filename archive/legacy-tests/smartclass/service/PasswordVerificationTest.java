package com.smartclass.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

class PasswordVerificationTest {

    @Test
    void testBcryptHashMatchesAdminPassword() {
        String password = "Admin@123";
        String hash = "$2a$10$1OPOCCoYVbGN/yHtp/rGceDTEw19rubu849gX8XzOveVv/TfWA6kK";
        boolean matches = BCrypt.checkpw(password, hash);
        assertTrue(matches, "Admin@123 should match the stored BCrypt hash");
    }

    @Test
    void testBcryptHashMatchesLecturerPassword() {
        String password = "Lecturer@123";
        String hash = "$2a$10$dFIjfFyAtrR3fi.D/6LA0ewdpaJ1tH7jr1KczeZRJGiXe34Xh3wEq";
        boolean matches = BCrypt.checkpw(password, hash);
        assertTrue(matches, "Lecturer@123 should match the stored BCrypt hash");
    }

    @Test
    void testBcryptHashMatchesStudentPassword() {
        String password = "Student@123";
        String hash = "$2a$10$DOQHRYwoyDk9IhpubFK9uunS2zBJ7swPIWKT3zKtZcoIQXANjnAnG";
        boolean matches = BCrypt.checkpw(password, hash);
        assertTrue(matches, "Student@123 should match the stored BCrypt hash");
    }

    @Test
    void testBcryptHashDoesNotMatchWrongPassword() {
        String password = "Wrong@123";
        String hash = "$2a$10$1OPOCCoYVbGN/yHtp/rGceDTEw19rubu849gX8XzOveVv/TfWA6kK";
        boolean matches = BCrypt.checkpw(password, hash);
        assertFalse(matches, "Wrong password should not match the stored hash");
    }

    @Test
    void testBcryptHashDoesNotMatchDifferentPassword() {
        String password = "Student@123";
        String hash = "$2a$10$1OPOCCoYVbGN/yHtp/rGceDTEw19rubu849gX8XzOveVv/TfWA6kK";
        boolean matches = BCrypt.checkpw(password, hash);
        assertFalse(matches, "Student password should not match admin hash");
    }
}