package com.smartclass.util;

import java.time.*;
import java.time.format.*;

public class TimezoneUtil {

    private static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    public static LocalDateTime toApplicationTimezone(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.of(DEFAULT_TIMEZONE);
        return dateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId).toLocalDateTime();
    }

    public static String getDefaultTimezone() {
        return DEFAULT_TIMEZONE;
    }

    public static void setAppTimezone(String timezone) {
        // Validate the timezone ID
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            // Keep default
        }
    }
}