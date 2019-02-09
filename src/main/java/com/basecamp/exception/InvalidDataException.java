package com.basecamp.exception;

public class InvalidDataException extends BaseException {

    public static final String ERROR = "Validation failed. ";

    public InvalidDataException(String message) {
        super(ERROR + message);
    }

    public InvalidDataException(final String fmt, final Object... args) {
        super(fmt, args);
    }

}