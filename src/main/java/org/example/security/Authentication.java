package org.example.security;

public interface Authentication {
    long getId();
    String getName(); // TODO: modify (чтобы можно было и id)
    boolean isAnonymous();
    default String[] getRoles() {
        return new String[]{};
    }
    default boolean hasRole(String role) {
        for (String r : getRoles()) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
