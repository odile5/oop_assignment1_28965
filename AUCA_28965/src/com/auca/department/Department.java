package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;
import com.auca.util.Check;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher; // optional: can be assigned later

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    public String getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getDepartmentCode() { return departmentCode; }
    public String getOfficeLocation() { return officeLocation; }
    public String getContactEmail() { return contactEmail; }
    public Teacher getHeadTeacher() { return headTeacher; }

    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }

    @Override
    public String getId() { return departmentId; }

    @Override
    public String getDisplayName() { return departmentName; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Department ID", departmentId);
        Check.name("Department name", departmentName);
        Check.text("Department code", departmentCode, 2, 10);
        Check.text("Office location", officeLocation, 2, 60);
        Check.email("Contact email", contactEmail);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "Department " + ID_SEPARATOR + " " + departmentId + " " + ID_SEPARATOR + " " + departmentName;
    }

    @Override
    public String toString() {
        return "[" + departmentId + "] " + departmentName + " (" + departmentCode + ") | office " + officeLocation
                + " | " + contactEmail + " | head: " + (headTeacher == null ? "(not assigned)" : headTeacher.getDisplayName());
    }
}
