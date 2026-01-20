package com.devops.cicd.user;

public class UserService {

    /**
     * Enregistre un utilisateur à partir des paramètres.
     *
     * Règles (voir spec) :
     * - crée un User
     * - renvoie l'utilisateur créé
     * - propage les erreurs si les données sont invalides
     */
    public User register(String email, String password, Role role) {
        return new User(email, password, role);
    }
}
