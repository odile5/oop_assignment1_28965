package com.auca.exceptions;

/** Custom exception: BookUnavailableException (unchecked, so workflows stay simple). */
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}
