package com.smartclass.model;

public class RoomStatusUpdate {
    private int id;
    private int classroomId;
    private String status;
    private String source; // TIMETABLE, BOOKING, MANUAL, SENSOR
    private String note;
    private java.time.LocalDateTime effectiveFrom;
    private java.time.LocalDateTime effectiveUntil;
    private Integer updatedBy;
    private java.time.LocalDateTime createdAt;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClassroomId() { return classroomId; }
    public void setClassroomId(int classroomId) { this.classroomId = classroomId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public java.time.LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(java.time.LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public java.time.LocalDateTime getEffectiveUntil() { return effectiveUntil; }
    public void setEffectiveUntil(java.time.LocalDateTime effectiveUntil) { this.effectiveUntil = effectiveUntil; }
    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}