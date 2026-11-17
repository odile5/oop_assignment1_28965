package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Check;
import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    public String getAssessmentId() { return assessmentId; }
    public Enrollment getEnrollment() { return enrollment; }
    public String getAssessmentType() { return assessmentType; }
    public double getMaximumScore() { return maximumScore; }
    public double getObtainedScore() { return obtainedScore; }
    public LocalDate getAssessmentDate() { return assessmentDate; }

    public void setObtainedScore(double obtainedScore) { this.obtainedScore = obtainedScore; }

    @Override
    public String getId() { return assessmentId; }

    @Override
    public String getDisplayName() { return assessmentType + " " + obtainedScore + "/" + maximumScore; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Assessment ID", assessmentId);
        Check.required("Enrollment", enrollment);
        Check.oneOf("Assessment type", assessmentType, Check.ASSESSMENT_TYPES);
        Check.greaterThanZero("Maximum score", maximumScore);
        Check.between("Obtained score", obtainedScore, 0, maximumScore);
        Check.notFuture("Assessment date", assessmentDate);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "Assessment " + ID_SEPARATOR + " " + assessmentId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + assessmentId + "] " + getDisplayName() + " | " + enrollment.getDisplayName()
                + " | " + assessmentDate;
    }
}
