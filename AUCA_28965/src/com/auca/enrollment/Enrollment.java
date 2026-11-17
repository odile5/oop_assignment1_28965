package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import com.auca.util.Check;
import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate,
                      String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public String getSemester() { return semester; }
    public String getEnrollmentStatus() { return enrollmentStatus; }

    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    @Override
    public String getId() { return enrollmentId; }

    @Override
    public String getDisplayName() {
        return student.getDisplayName() + " in " + course.getCourseCode() + " (" + semester + ")";
    }

    @Override
    public boolean isActive() { return "ACTIVE".equalsIgnoreCase(enrollmentStatus); }

    @Override
    public void validate() throws ValidationException {
        Check.id("Enrollment ID", enrollmentId);
        Check.required("Student", student);
        Check.required("Course", course);
        Check.notFuture("Enrollment date", enrollmentDate);
        Check.oneOf("Semester", semester, Check.SEMESTERS);
        Check.oneOf("Enrollment status", enrollmentStatus, Check.ENROLLMENT_STATUSES);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "Enrollment " + ID_SEPARATOR + " " + enrollmentId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + enrollmentId + "] " + getDisplayName() + " | " + enrollmentDate + " | " + enrollmentStatus;
    }
}
