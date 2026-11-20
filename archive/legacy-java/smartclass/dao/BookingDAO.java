package com.smartclass.dao;

import com.smartclass.model.Booking;
import java.util.List;

public interface BookingDAO {
    List<Booking> findAll();
    List<Booking> findByClassroomId(int classroomId);
    List<Booking> findByStatus(String status);
    Booking findById(int id);
    Booking findOverlappingBooking(int classroomId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    Booking create(Booking booking);
    boolean update(Booking booking);
    boolean delete(int id);
    boolean approve(Booking booking, int reviewerId, String note);
}