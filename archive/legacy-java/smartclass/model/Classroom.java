package com.smartclass.model;

public class Classroom {
    private int id;
    private int buildingId;
    private Integer departmentId;
    private String roomNumber;
    private String roomName;
    private int floorNumber;
    private int capacity;
    private String roomType;
    private double coordinateX;
    private double coordinateY;
    private double coordinateZ;
    private boolean active;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private String status;
    private Building building;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBuildingId() { return buildingId; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public double getCoordinateX() { return coordinateX; }
    public void setCoordinateX(double coordinateX) { this.coordinateX = coordinateX; }
    public double getCoordinateY() { return coordinateY; }
    public void setCoordinateY(double coordinateY) { this.coordinateY = coordinateY; }
    public double getCoordinateZ() { return coordinateZ; }
    public void setCoordinateZ(double coordinateZ) { this.coordinateZ = coordinateZ; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
}