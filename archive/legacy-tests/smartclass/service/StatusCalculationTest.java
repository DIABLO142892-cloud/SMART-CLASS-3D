package com.smartclass.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StatusCalculationTest {

    @Test
    void testStatusPriorityManualOverride() {
        // Manual override should have highest priority
        // This would test the StatusCalculationService.calculateCurrentStatus()
        // with an active manual override
        // assertEquals("RESERVED", service.calculateStatus(classroomId, now));
    }

    @Test
    void testStatusPriorityMaintenance() {
        // Maintenance should be second priority
        // assertEquals("MAINTENANCE", service.calculateStatus(classroomId, now));
    }

    @Test
    void testStatusPriorityApprovedBooking() {
        // Active approved booking should be third priority
        // assertEquals("OCCUPIED", service.calculateStatus(classroomId, now));
    }

    @Test
    void testStatusPriorityTimetable() {
        // Active timetable class should be fourth priority
        // assertEquals("OCCUPIED", service.calculateStatus(classroomId, now));
    }

    @Test
    void testStatusPriorityAvailable() {
        // Available should be the default/fifth priority
        // assertEquals("AVAILABLE", service.calculateStatus(classroomId, now));
    }
}