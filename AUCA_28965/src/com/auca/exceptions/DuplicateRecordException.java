package com.auca.exceptions;

/** Custom exception: DuplicateRecordException (unchecked, so workflows stay simple). */
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
