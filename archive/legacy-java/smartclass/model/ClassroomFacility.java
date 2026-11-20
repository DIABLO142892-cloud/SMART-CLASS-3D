package com.smartclass.model;

public class ClassroomFacility {
    private int classroomId;
    private int facilityId;
    private int quantity;
    private boolean working;

    // Getters and setters
    public int getClassroomId() { return classroomId; }
    public void setClassroomId(int classroomId) { this.classroomId = classroomId; }
    public int getFacilityId() { return facilityId; }
    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isWorking() { return working; }
    public void setWorking(boolean working) { this.working = working; }
}