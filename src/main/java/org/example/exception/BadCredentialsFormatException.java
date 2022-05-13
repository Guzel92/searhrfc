package org.example.exception;

public class BadCredentialsFormatException extends RuntimeException {
    public BadCredentialsFormatException() {
    }

    public BadCredentialsFormatException(String message) {
        super(message);
    }

    public BadCredentialsFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadCredentialsFormatException(Throwable cause) {
        super(cause);
    }

    public BadCredentialsFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
