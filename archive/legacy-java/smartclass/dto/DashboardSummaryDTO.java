package com.smartclass.dto;

public class DashboardSummaryDTO {
    private int totalClassrooms;
    private int available;
    private int occupied;
    private int reserved;
    private int maintenance;
    private double utilizationPercentage;
    private java.time.LocalDateTime currentTime;

    // Getters and setters
    public int getTotalClassrooms() { return totalClassrooms; }
    public void setTotalClassrooms(int totalClassrooms) { this.totalClassrooms = totalClassrooms; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    public int getReserved() { return reserved; }
    public void setReserved(int reserved) { this.reserved = reserved; }
    public int getMaintenance() { return maintenance; }
    public void setMaintenance(int maintenance) { this.maintenance = maintenance; }
    public double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }
    public java.time.LocalDateTime getCurrentTime() { return currentTime; }
    public void setCurrentTime(java.time.LocalDateTime currentTime) { this.currentTime = currentTime; }
}