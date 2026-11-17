package com.auca.exceptions;

/** Thrown when the user types CANCEL or chooses Cancel: the form is abandoned and nothing is saved. */
public class CancelException extends RuntimeException {
    public CancelException() {
        super("Cancelled by user.");
    }
}
