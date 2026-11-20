package com.smartclass.dto;

import com.smartclass.model.Classroom;
import com.smartclass.model.Building;
import com.smartclass.model.Facility;
import java.util.List;

public class ClassroomDTO {
    private Classroom classroom;
    private Building building;
    private List<Facility> facilities;
    private boolean isFavorite;
    private String availabilityStatus;
    private String availabilityUntil;
    private double utilizationPercentage;

    // Getters and setters
    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
    public List<Facility> getFacilities() { return facilities; }
    public void setFacilities(List<Facility> facilities) { this.facilities = facilities; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public String getAvailabilityUntil() { return availabilityUntil; }
    public void setAvailabilityUntil(String availabilityUntil) { this.availabilityUntil = availabilityUntil; }
    public double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }
}