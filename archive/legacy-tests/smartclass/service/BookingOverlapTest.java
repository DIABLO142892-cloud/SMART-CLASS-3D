package com.smartclass.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class BookingOverlapTest {

    @Test
    void testNoOverlapWhenBookingsDontIntersect() {
        // Booking A: 10:00 - 12:00
        // Booking B: 13:00 - 15:00
        // These should not overlap
    }

    @Test
    void testOverlapWhenBookingsIntersect() {
        // Booking A: 10:00 - 12:00
        // Booking B: 11:00 - 13:00
        // These should overlap
    }

    @Test
    void testNoOverlapWhenOneEndsWhenOtherStarts() {
        // Booking A: 10:00 - 12:00
        // Booking B: 12:00 - 14:00
        // These should not overlap (end time equals start time)
    }

    @Test
    void testPastBookingRejected() {
        // A booking starting in the past should be rejected
    }

    @Test
    void testEndBeforeStartRejected() {
        // A booking where end is before start should be rejected
    }
}