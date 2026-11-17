package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Check;

public class LibraryBook implements Identifiable, Registrable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author,
                       int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }

    /** Used by borrow (-1) and return (+1). Refuses values below 0 or above totalCopies. */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = Check.range("Available copies", availableCopies, 0, totalCopies);
    }

    @Override
    public String getId() { return bookId; }

    @Override
    public String getDisplayName() { return title; }

    @Override
    public boolean isActive() { return availableCopies > 0; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Book ID", bookId);
        Check.text("ISBN", isbn, 10, 17);
        Check.text("Title", title, 1, 120);
        Check.text("Author", author, 2, 120);
        Check.range("Total copies", totalCopies, 0, Integer.MAX_VALUE);
        Check.range("Available copies", availableCopies, 0, totalCopies);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return "LibraryBook " + ID_SEPARATOR + " " + bookId + " " + ID_SEPARATOR + " " + title;
    }

    @Override
    public String toString() {
        return "[" + bookId + "] " + title + " by " + author + " | ISBN " + isbn + " | copies "
                + availableCopies + "/" + totalCopies + " available";
    }
}
