package com.smartclass.dao;

import com.smartclass.model.RoomStatusUpdate;
import java.util.List;

public interface RoomStatusUpdateDAO {
    List<RoomStatusUpdate> findByClassroomId(int classroomId);
    List<RoomStatusUpdate> findActiveUpdate(int classroomId, java.time.LocalDateTime now);
    RoomStatusUpdate findById(int id);
    RoomStatusUpdate create(RoomStatusUpdate statusUpdate);
    boolean update(RoomStatusUpdate statusUpdate);
    boolean delete(int id);
    boolean overrideStatus(int classroomId, String status, String source, Integer updatedBy);
}