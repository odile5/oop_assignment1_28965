package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Check;

public class Classroom implements Identifiable, Registrable {
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber,
                     int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    public String getClassroomId() { return classroomId; }
    public String getBuildingName() { return buildingName; }
    public String getRoomNumber() { return roomNumber; }
    public int getCapacity() { return capacity; }
    public int getFloorNumber() { return floorNumber; }

    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String getId() { return classroomId; }

    @Override
    public String getDisplayName() { return buildingName + " room " + roomNumber; }

    @Override
    public boolean isActive() { return available; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Classroom ID", classroomId);
        Check.text("Building name", buildingName, 2, 60);
        Check.text("Room number", roomNumber, 1, 20);
        Check.range("Capacity", capacity, 1, Integer.MAX_VALUE);
        Check.range("Floor number", floorNumber, 0, Integer.MAX_VALUE);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "Classroom " + ID_SEPARATOR + " " + classroomId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + classroomId + "] " + getDisplayName() + " | floor " + floorNumber + " | capacity " + capacity
                + " | available " + (available ? "YES" : "NO");
    }
}
