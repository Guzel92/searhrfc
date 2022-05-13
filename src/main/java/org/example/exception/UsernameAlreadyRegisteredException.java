package org.example.exception;

public class UsernameAlreadyRegisteredException extends RuntimeException {
    public UsernameAlreadyRegisteredException() {
    }

    public UsernameAlreadyRegisteredException(String message) {
        super(message);
    }

    public UsernameAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyRegisteredException(Throwable cause) {
        super(cause);
    }

    public UsernameAlreadyRegisteredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
