package com.example;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class Hasher {
    public static void main(String[] args) {
        final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        // при регистрации
        final String hash = encoder.encode("secret");
        System.out.println("hash = " + hash);
        // при аутентификации (hash берём из БД)
        final boolean matches = encoder.matches("secret", hash);
        System.out.println("matches = " + matches);
    }
}
