package com.smartclass.model;

public class Building {
    private int id;
    private String code;
    private String name;
    private String description;
    private int totalFloors;
    private double coordinateX;
    private double coordinateY;
    private double coordinateZ;
    private boolean active;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }
    public double getCoordinateX() { return coordinateX; }
    public void setCoordinateX(double coordinateX) { this.coordinateX = coordinateX; }
    public double getCoordinateY() { return coordinateY; }
    public void setCoordinateY(double coordinateY) { this.coordinateY = coordinateY; }
    public double getCoordinateZ() { return coordinateZ; }
    public void setCoordinateZ(double coordinateZ) { this.coordinateZ = coordinateZ; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}