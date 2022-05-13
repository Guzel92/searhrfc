package org.example.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenAuthentication implements Authentication {
    private final long id;
    private final String login;
    private final String[] roles;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return login;
    }

    @Override
    public String[] getRoles() {
        return roles;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }
}
