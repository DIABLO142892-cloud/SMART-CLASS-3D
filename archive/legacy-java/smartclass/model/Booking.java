package com.smartclass.model;

public class Booking {
    private int id;
    private int classroomId;
    private Integer requestedBy;
    private String purpose;
    private java.time.LocalDateTime startDateTime;
    private java.time.LocalDateTime endDateTime;
    private String status;
    private Integer reviewedBy;
    private String reviewNote;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClassroomId() { return classroomId; }
    public void setClassroomId(int classroomId) { this.classroomId = classroomId; }
    public Integer getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Integer requestedBy) { this.requestedBy = requestedBy; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public java.time.LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(java.time.LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public java.time.LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(java.time.LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}