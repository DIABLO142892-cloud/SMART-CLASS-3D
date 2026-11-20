package com.smartclass.app;

import java.time.LocalDateTime;
import java.util.Set;

public final class Validation {
    private static final Set<String> ROLES = Set.of("ADMIN", "LECTURER", "STUDENT");
    private static final Set<String> ROOM_STATUSES = Set.of("AVAILABLE", "OCCUPIED", "RESERVED", "MAINTENANCE");

    private Validation() {}

    public static String required(Object value, String field, int max) {
        String text = value == null ? "" : value.toString().trim();
        if (text.isEmpty()) throw new AppException(400, field + " is required");
        if (text.length() > max) throw new AppException(400, field + " is too long");
        return text;
    }

    public static int positiveInt(Object value, String field) {
        try {
            int number = Integer.parseInt(required(value, field, 12));
            if (number <= 0) throw new NumberFormatException();
            return number;
        } catch (NumberFormatException ex) {
            throw new AppException(400, field + " must be a positive number");
        }
    }

    public static LocalDateTime dateTime(Object value, String field) {
        try {
            return LocalDateTime.parse(required(value, field, 30));
        } catch (RuntimeException ex) {
            if (ex instanceof AppException) throw ex;
            throw new AppException(400, field + " must be a valid date and time");
        }
    }

    public static String role(Object value) {
        String role = required(value, "role", 20).toUpperCase();
        if (!ROLES.contains(role)) throw new AppException(400, "Invalid role");
        return role;
    }

    public static String roomStatus(Object value) {
        String status = required(value, "status", 20).toUpperCase();
        if (!ROOM_STATUSES.contains(status)) throw new AppException(400, "Invalid classroom status");
        return status;
    }
}
