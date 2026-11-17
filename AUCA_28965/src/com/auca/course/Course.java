package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;
import com.auca.util.Check;

public class Course implements Identifiable, Registrable {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours,
                  Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCreditHours() { return creditHours; }
    public Department getDepartment() { return department; }
    public Teacher getAssignedTeacher() { return assignedTeacher; }

    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }
    public void setAssignedTeacher(Teacher assignedTeacher) { this.assignedTeacher = assignedTeacher; }

    @Override
    public String getId() { return courseId; }

    @Override
    public String getDisplayName() { return courseCode + " - " + courseTitle; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Course ID", courseId);
        Check.text("Course code", courseCode, 2, 20);
        Check.text("Course title", courseTitle, 2, 120);
        Check.range("Credit hours", creditHours, 1, 6);
        Check.required("Department", department);
        Check.required("Teacher", assignedTeacher);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "Course " + ID_SEPARATOR + " " + courseId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + courseId + "] " + getDisplayName() + " | " + creditHours + " credits | dept "
                + department.getDepartmentName() + " | teacher " + assignedTeacher.getDisplayName();
    }
}
