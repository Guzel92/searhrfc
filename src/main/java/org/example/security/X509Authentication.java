package org.example.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class X509Authentication implements Authentication {
    private final long id;
    private final String commonName;
    private final String[] roles;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return commonName;
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
