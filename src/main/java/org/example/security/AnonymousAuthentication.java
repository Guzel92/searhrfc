package org.example.security;

public class AnonymousAuthentication implements Authentication {
    public static final String ANONYMOUS = "anonymous";

    @Override
    public long getId() {
return -1;
    }

    @Override
    public String getName() {
        return ANONYMOUS; // ctrl + alt + c - public static final ...
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }
}
