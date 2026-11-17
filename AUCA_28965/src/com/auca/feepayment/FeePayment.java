package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import com.auca.util.Check;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate,
                      String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    public String getPaymentId() { return paymentId; }
    public Student getStudent() { return student; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReceiptNumber() { return receiptNumber; }

    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String getId() { return paymentId; }

    @Override
    public String getDisplayName() { return "Receipt " + receiptNumber + " (" + amount + ")"; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Payment ID", paymentId);
        Check.required("Student", student);
        Check.positiveMoney("Amount", amount);
        Check.notFuture("Payment date", paymentDate);
        Check.oneOf("Payment method", paymentMethod, Check.PAYMENT_METHODS);
        Check.text("Receipt number", receiptNumber, 1, 30);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "FeePayment " + ID_SEPARATOR + " " + paymentId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + paymentId + "] " + student.getDisplayName() + " paid " + amount + " on " + paymentDate
                + " by " + paymentMethod + " | receipt " + receiptNumber;
    }
}
