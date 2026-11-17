package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.util.Check;
import java.time.LocalDate;

public class Student extends AcademicMember {
    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName, String email, String phoneNumber,
                   LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String studentNumber, String programName, LocalDate admissionDate, int studyYear,
                   String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department,
                joinDate, academicStatus, institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    public String getStudentNumber() { return studentNumber; }
    public String getProgramName() { return programName; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public int getStudyYear() { return studyYear; }
    public String getGuardianPhone() { return guardianPhone; }
    public boolean isScholarshipStatus() { return scholarshipStatus; }

    public void setProgramName(String programName) { this.programName = programName; }
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
    public void setScholarshipStatus(boolean scholarshipStatus) { this.scholarshipStatus = scholarshipStatus; }

    @Override
    public String getRoleDescription() { return "Student"; }

    @Override
    public String getAcademicCategory() { return "Learner"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Check.id("Student number", studentNumber);
        Check.text("Program name", programName, 2, 120);
        Check.notFuture("Admission date", admissionDate);
        Check.range("Study year", studyYear, 1, 6);
        Check.phone("Guardian phone", guardianPhone);
    }

    @Override
    public String toString() {
        return super.toString() + " | student " + studentNumber + " | " + programName + " year " + studyYear
                + " | scholarship " + (scholarshipStatus ? "YES" : "NO");
    }
}
