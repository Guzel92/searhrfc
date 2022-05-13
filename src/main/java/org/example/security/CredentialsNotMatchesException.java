package org.example.security;

public class CredentialsNotMatchesException extends AuthenticationException {
    public CredentialsNotMatchesException() {
    }

    public CredentialsNotMatchesException(String message) {
        super(message);
    }

    public CredentialsNotMatchesException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsNotMatchesException(Throwable cause) {
        super(cause);
    }

    public CredentialsNotMatchesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
