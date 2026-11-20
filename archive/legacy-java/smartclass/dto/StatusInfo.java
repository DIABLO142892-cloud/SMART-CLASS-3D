package com.smartclass.dto;

public class StatusInfo {
    private String status;
    private String source;
    private java.time.LocalDateTime effectiveFrom;
    private java.time.LocalDateTime effectiveUntil;
    private String note;

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public java.time.LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(java.time.LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public java.time.LocalDateTime getEffectiveUntil() { return effectiveUntil; }
    public void setEffectiveUntil(java.time.LocalDateTime effectiveUntil) { this.effectiveUntil = effectiveUntil; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}