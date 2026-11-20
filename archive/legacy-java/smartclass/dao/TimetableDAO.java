package com.smartclass.dao;

import com.smartclass.model.Timetable;
import java.util.List;

public interface TimetableDAO {
    List<Timetable> findAll();
    List<Timetable> findByClassroomId(int classroomId);
    List<Timetable> findActiveTimetable(int classroomId, java.time.LocalDate date, java.time.LocalTime time);
    Timetable findByClassroomAndDayTime(int classroomId, String dayOfWeek, String startTime);
    Timetable create(Timetable timetable);
    boolean update(Timetable timetable);
    boolean delete(int id);
}