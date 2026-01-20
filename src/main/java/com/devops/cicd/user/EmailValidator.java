package com.devops.cicd.user;

public final class EmailValidator {

    private EmailValidator() {}

    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String trimmedEmail = email.trim();
        return trimmedEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
