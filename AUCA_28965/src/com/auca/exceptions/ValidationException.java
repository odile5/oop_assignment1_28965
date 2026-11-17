package com.auca.exceptions;

/** Custom exception: ValidationException (unchecked, so workflows stay simple). */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
