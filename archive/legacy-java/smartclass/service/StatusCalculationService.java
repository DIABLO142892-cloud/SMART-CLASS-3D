package com.smartclass.service;

import com.smartclass.model.Classroom;
import com.smartclass.model.RoomStatusUpdate;
import com.smartclass.model.Timetable;
import com.smartclass.model.Booking;
import com.smartclass.model.User;
import com.smartclass.dto.StatusInfo;
import com.smartclass.dao.ClassroomDAO;
import com.smartclass.dao.RoomStatusUpdateDAO;
import com.smartclass.dao.TimetableDAO;
import com.smartclass.dao.BookingDAO;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;

public class StatusCalculationService {

    private final ClassroomDAO classroomDAO;
    private final RoomStatusUpdateDAO statusUpdateDAO;
    private final TimetableDAO timetableDAO;
    private final BookingDAO bookingDAO;

    public StatusCalculationService(ClassroomDAO classroomDAO,
                                    RoomStatusUpdateDAO statusUpdateDAO,
                                    TimetableDAO timetableDAO,
                                    BookingDAO bookingDAO) {
        this.classroomDAO = classroomDAO;
        this.statusUpdateDAO = statusUpdateDAO;
        this.timetableDAO = timetableDAO;
        this.bookingDAO = bookingDAO;
    }

    /**
     * Calculate the current status of a classroom with priority order:
     * 1. Active manual override
     * 2. Maintenance status
     * 3. Active booking
     * 4. Active timetable class
     * 5. Available
     */
    public String calculateCurrentStatus(int classroomId, LocalDateTime now) {
        now = applyTimezone(now);

        // 1. Check for active manual override
        List<RoomStatusUpdate> activeOverrides = statusUpdateDAO.findActiveUpdate(classroomId, now);
        if (activeOverrides != null && !activeOverrides.isEmpty()) {
            RoomStatusUpdate activeOverride = activeOverrides.get(0);
            if (activeOverride.getEffectiveUntil() != null && now.isAfter(activeOverride.getEffectiveUntil())) {
                // Override has expired, continue to next priority
            } else {
                return activeOverride.getStatus();
            }
        }

        // 2. Check maintenance status
        List<RoomStatusUpdate> maintenanceUpdates = statusUpdateDAO.findByClassroomId(classroomId);
        for (RoomStatusUpdate update : maintenanceUpdates) {
            if ("MAINTENANCE".equals(update.getStatus())) {
                if (now.isAfter(update.getEffectiveFrom())
                        && (update.getEffectiveUntil() == null || !now.isAfter(update.getEffectiveUntil()))) {
                    return "MAINTENANCE";
                }
            }
        }

        // 3. Check for active approved booking
        List<Booking> activeBookings = bookingDAO.findByClassroomId(classroomId);
        for (Booking booking : activeBookings) {
            if ("APPROVED".equals(booking.getStatus())) {
                if (now.isAfter(booking.getStartDateTime())
                        && now.isBefore(booking.getEndDateTime())) {
                    return "OCCUPIED";
                }
            }
        }

        // 4. Check for active timetable class
        List<Timetable> activeClasses = timetableDAO.findActiveTimetable(classroomId, now.toLocalDate(), now.toLocalTime());
        for (Timetable timetable : activeClasses) {
            if ("ACTIVE".equals(timetable.getStatus())) {
                return "OCCUPIED";
            }
        }

        // 5. Available
        return "AVAILABLE";
    }

    /**
     * Get detailed status information for a classroom
     */
    public StatusInfo getStatusDetails(int classroomId, LocalDateTime now) {
        now = applyTimezone(now);
        StatusInfo info = new StatusInfo();

        // Check manual override first
        List<RoomStatusUpdate> activeOverrides = statusUpdateDAO.findActiveUpdate(classroomId, now);
        if (activeOverrides != null && !activeOverrides.isEmpty()) {
            RoomStatusUpdate activeOverride = activeOverrides.get(0);
            if (now.isAfter(activeOverride.getEffectiveUntil())) {
                // Expired, continue checking
            } else {
                info.setStatus(activeOverride.getStatus());
                info.setSource(activeOverride.getSource());
                info.setEffectiveFrom(activeOverride.getEffectiveFrom());
                info.setEffectiveUntil(activeOverride.getEffectiveUntil());
                info.setNote(activeOverride.getNote());
                return info;
            }
        }

        // Check maintenance
        List<RoomStatusUpdate> maintenanceUpdates = statusUpdateDAO.findByClassroomId(classroomId);
        for (RoomStatusUpdate update : maintenanceUpdates) {
            if ("MAINTENANCE".equals(update.getStatus())) {
                if (now.isAfter(update.getEffectiveFrom())
                        && (update.getEffectiveUntil() == null || !now.isAfter(update.getEffectiveUntil()))) {
                    info.setStatus("MAINTENANCE");
                    info.setSource(update.getSource());
                    info.setEffectiveFrom(update.getEffectiveFrom());
                    info.setEffectiveUntil(update.getEffectiveUntil());
                    info.setNote(update.getNote());
                    return info;
                }
            }
        }

        // Check active booking
        List<Booking> activeBookings = bookingDAO.findByClassroomId(classroomId);
        for (Booking booking : activeBookings) {
            if ("APPROVED".equals(booking.getStatus())) {
                if (now.isAfter(booking.getStartDateTime())
                        && now.isBefore(booking.getEndDateTime())) {
                    info.setStatus("OCCUPIED");
                    info.setSource("BOOKING");
                    info.setEffectiveFrom(booking.getStartDateTime());
                    info.setEffectiveUntil(booking.getEndDateTime());
                    info.setNote("Booked");
                    return info;
                }
            }
        }

        // Check timetable
        List<Timetable> activeClasses = timetableDAO.findActiveTimetable(classroomId, now.toLocalDate(), now.toLocalTime());
        for (Timetable timetable : activeClasses) {
            if ("ACTIVE".equals(timetable.getStatus())) {
                info.setStatus("OCCUPIED");
                info.setSource("TIMETABLE");
                info.setEffectiveFrom(timetable.getValidFrom() != null ? timetable.getValidFrom().atStartOfDay() : null);
                info.setEffectiveUntil(timetable.getValidUntil() != null ? timetable.getValidUntil().atStartOfDay() : null);
                info.setNote("Class: " + timetable.getSubjectName() + " (" + timetable.getSectionName() + ")");
                return info;
            }
        }

        // Available
        info.setStatus("AVAILABLE");
        info.setSource("TIMETABLE");
        info.setNote("No scheduled classes or bookings");
        return info;
    }

    /**
     * Calculate how long a room will remain available
     */
    public String getAvailabilityDuration(int classroomId, LocalDateTime now) {
        now = applyTimezone(now);
        String status = calculateCurrentStatus(classroomId, now);

        if ("AVAILABLE".equals(status)) {
            // Check for upcoming bookings that will occupy the room
            List<Booking> upcomingBookings = bookingDAO.findByClassroomId(classroomId);
            for (Booking booking : upcomingBookings) {
                if ("APPROVED".equals(booking.getStatus())) {
                    if (now.isBefore(booking.getStartDateTime())) {
                        long minutes = java.time.Duration.between(now, booking.getStartDateTime()).toMinutes();
                        if (minutes > 0 && minutes < 1440) { // Within 24 hours
                            if (minutes < 60) {
                                return minutes + " minute(s) until occupied";
                            } else {
                                long hrs = minutes / 60;
                                return hrs + " hour(s) until occupied";
                            }
                        }
                    }
                }
            }

            // Check timetable for next class
            List<Timetable> nextClasses = timetableDAO.findActiveTimetable(classroomId, now.toLocalDate(), now.toLocalTime());

            return "Available until next scheduled class";
        }

        return "Currently " + status.toLowerCase();
    }

    /**
     * Detect overlapping bookings for the same classroom
     */
    public boolean hasOverlappingBooking(int classroomId, LocalDateTime newStart, LocalDateTime newEnd) {
        List<Booking> bookings = bookingDAO.findByClassroomId(classroomId);
        for (Booking booking : bookings) {
            if ("APPROVED".equals(booking.getStatus())) {
                // Check overlap: new booking starts before existing ends
                // AND new booking ends after existing starts
                if (!newEnd.isBefore(booking.getStartDateTime())
                        && !newStart.isAfter(booking.getEndDateTime())) {
                    return true; // Overlapping
                }
            }
        }
        return false;
    }

    /**
     * Apply consistent application timezone
     */
    private LocalDateTime applyTimezone(LocalDateTime dateTime) {
        // Default to Asia/Kolkata if not configured
        String tz = System.getProperty("APP_TIMEZONE", "Asia/Kolkata");
        try {
            ZoneId zoneId = ZoneId.of(tz);
            return dateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId).toLocalDateTime();
        } catch (Exception e) {
            return dateTime;
        }
    }
}