package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.util.Check;
import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {
    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate; // null until the book is returned

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate,
                    LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    public String getLoanId() { return loanId; }
    public Student getStudent() { return student; }
    public LibraryBook getBook() { return book; }
    public LocalDate getBorrowedDate() { return borrowedDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    @Override
    public String getId() { return loanId; }

    @Override
    public String getDisplayName() { return student.getDisplayName() + " - " + book.getTitle(); }

    /** A loan is "active" while the book has not been returned. */
    @Override
    public boolean isActive() { return returnedDate == null; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Loan ID", loanId);
        Check.required("Student", student);
        Check.required("Book", book);
        Check.notFuture("Borrowed date", borrowedDate);
        Check.required("Due date", dueDate);
        if (!dueDate.isAfter(borrowedDate)) {
            throw new ValidationException("Due date must be after the borrowed date.");
        }
        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned date cannot be before the borrowed date.");
        }
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "BookLoan " + ID_SEPARATOR + " " + loanId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + loanId + "] " + getDisplayName() + " | borrowed " + borrowedDate + " | due " + dueDate
                + " | " + (returnedDate == null ? "NOT returned" : "returned " + returnedDate);
    }
}
