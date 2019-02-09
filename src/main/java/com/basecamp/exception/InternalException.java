package com.basecamp.exception;

public class InternalException extends BaseException {

    public static final String ERROR = "Internal exception. ";

    public InternalException(String message) {
        super(ERROR + message);
    }

    public InternalException(final String fmt, final Object... args) {
        super(fmt, args);
    }

}