package com.smartclass.dto;

public class BookingDTO {
    private int id;
    private String classroomName;
    private java.time.LocalDateTime startDateTime;
    private java.time.LocalDateTime endDateTime;
    private String status;
    private String purpose;
    private String reviewerName;
    private String reviewNote;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
    public java.time.LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(java.time.LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public java.time.LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(java.time.LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
}