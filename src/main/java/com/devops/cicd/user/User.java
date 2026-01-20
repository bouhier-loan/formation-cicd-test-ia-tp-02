package com.devops.cicd.user;

import com.devops.cicd.PasswordPolicy;

public class User {

    private final String email;
    private final String password;
    private final Role role;

    public User(String email, String password, Role role) {
        // - email: obligatoire, trim, format simple
        // - password: obligatoire, strong (PasswordPolicy.isStrong)
        // - role: obligatoire (non null)
        //
        // En cas d'erreur: IllegalArgumentException avec un message explicite
        // ("email must be valid", "password must be strong", "role must not be null")

        this.email = normalizeEmail(email);
        this.password = checkPassword(password);
        this.role = checkRole(role);
    }

    private Role checkRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("role must not be null");
        }
        return role;
    }

    private String checkPassword(String password) {
        if (!PasswordPolicy.isStrong(password)) {
            throw new IllegalArgumentException("password must be strong");
        }
        return password;
    }

    private String normalizeEmail(String email) {
        if (!EmailValidator.isValid(email)) {
            throw new IllegalArgumentException("email must be valid");
        } else {
            return email.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean canAccessAdminArea() {
        return this.role == Role.ADMIN;
    }

    // BONUS: vous pouvez ajouter equals/hashCode/toString si utile (non obligatoire)
}
