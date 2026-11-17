package com.auca.exceptions;

/** Custom exception: RecordNotFoundException (unchecked, so workflows stay simple). */
public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}
