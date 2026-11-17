package com.auca.exceptions;

/** Custom exception: PrerequisiteMissingException (unchecked, so workflows stay simple). */
public class PrerequisiteMissingException extends RuntimeException {
    public PrerequisiteMissingException(String message) {
        super(message);
    }
}
