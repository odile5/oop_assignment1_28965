package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.util.Check;
import java.time.LocalDate;

/** Abstract middle level: Person -> AcademicMember -> Student / Teacher (multilevel inheritance). */
public abstract class AcademicMember extends Person {
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    public AcademicMember(String personId, String firstName, String lastName, String email,
                          String phoneNumber, LocalDate dateOfBirth, String memberNumber,
                          Department department, LocalDate joinDate, String academicStatus,
                          String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth); // constructor chaining
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public String getMemberNumber() { return memberNumber; }
    public Department getDepartment() { return department; }
    public LocalDate getJoinDate() { return joinDate; }
    public String getAcademicStatus() { return academicStatus; }
    public String getInstitutionalEmail() { return institutionalEmail; }
    public String getAdvisorName() { return advisorName; }

    public void setDepartment(Department department) { this.department = department; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }
    public void setInstitutionalEmail(String institutionalEmail) { this.institutionalEmail = institutionalEmail; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    public abstract String getAcademicCategory();

    @Override
    public boolean isActive() { return "ACTIVE".equalsIgnoreCase(academicStatus); }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Check.id("Member number", memberNumber);
        Check.required("Department", department);
        Check.notFuture("Join date", joinDate);
        Check.oneOf("Academic status", academicStatus, Check.ACADEMIC_STATUSES);
        Check.email("Institutional email", institutionalEmail);
        Check.text("Advisor name", advisorName, 2, 120);
    }

    @Override
    public String toString() {
        return super.toString() + " | member " + memberNumber + " | dept " + department.getDepartmentName()
                + " | " + academicStatus;
    }
}
