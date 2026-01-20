package com.devops.cicd.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // ========== Constantes pour les tests ==========
    private static final String VALID_EMAIL = "alice@test.com";
    private static final String VALID_PASSWORD = "Password1!";
    private static final Role VALID_ROLE = Role.USER;

    // ========== Tests de validation de l'email ==========
    @Nested
    @DisplayName("Tests de validation de l'email")
    class EmailValidationTests {

        @ParameterizedTest
        @DisplayName("Doit accepter les emails valides")
        @ValueSource(strings = {
                "alice@test.com",
                "bob.smith@company.io",
                "user@domain.org",
                "test.user@sub.domain.com"
        })
        void shouldAcceptValidEmails(String email) {
            User user = new User(email, VALID_PASSWORD, VALID_ROLE);
            assertNotNull(user);
            assertEquals(email.trim(), user.getEmail());
        }

        @Test
        @DisplayName("Doit normaliser l'email avec trim()")
        void shouldTrimEmail() {
            String emailWithSpaces = "  alice@test.com  ";
            User user = new User(emailWithSpaces, VALID_PASSWORD, VALID_ROLE);
            assertEquals("alice@test.com", user.getEmail());
        }

        @ParameterizedTest
        @DisplayName("Doit rejeter les emails invalides")
        @NullSource
        @ValueSource(strings = {
                "   ",
                "alice",
                "alice@",
                "@test.com",
                "alice@test",
                "alice@@test.com"
        })
        void shouldRejectInvalidEmails(String email) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User(email, VALID_PASSWORD, VALID_ROLE)
            );
            assertEquals("email must be valid", exception.getMessage());
        }
    }

    // ========== Tests de validation du password ==========
    @Nested
    @DisplayName("Tests de validation du password")
    class PasswordValidationTests {

        @ParameterizedTest
        @DisplayName("Doit accepter les mots de passe forts")
        @ValueSource(strings = {
                "Password1!",
                "Str0ng@Pass",
                "Abcdefg1!",
                "Test1234#"
        })
        void shouldAcceptStrongPasswords(String password) {
            User user = new User(VALID_EMAIL, password, VALID_ROLE);
            assertNotNull(user);
            assertEquals(password, user.getPassword());
        }

        @Test
        @DisplayName("Le password ne doit pas être modifié (pas de trim)")
        void shouldNotModifyPassword() {
            String passwordWithSpaces = "  Password1!  ";
            User user = new User(VALID_EMAIL, passwordWithSpaces, VALID_ROLE);
            assertEquals(passwordWithSpaces, user.getPassword());
        }

        @ParameterizedTest
        @DisplayName("Doit rejeter les mots de passe faibles")
        @NullSource
        @ValueSource(strings = {
                "",
                "   ",
                "short1!",           // trop court (< 8 caractères)
                "password",          // pas de majuscule, pas de chiffre, pas de caractère spécial
                "PASSWORD",          // pas de minuscule, pas de chiffre, pas de caractère spécial
                "Password",          // pas de chiffre, pas de caractère spécial
                "Password1",         // pas de caractère spécial
                "password1!",        // pas de majuscule
                "PASSWORD1!",        // pas de minuscule
                "12345678"           // pas de lettre, pas de caractère spécial
        })
        void shouldRejectWeakPasswords(String password) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User(VALID_EMAIL, password, VALID_ROLE)
            );
            assertEquals("password must be strong", exception.getMessage());
        }
    }

    // ========== Tests de validation du role ==========
    @Nested
    @DisplayName("Tests de validation du role")
    class RoleValidationTests {

        @ParameterizedTest
        @DisplayName("Doit accepter les rôles valides USER et ADMIN")
        @MethodSource("provideValidRoles")
        void shouldAcceptValidRoles(Role role) {
            User user = new User(VALID_EMAIL, VALID_PASSWORD, role);
            assertNotNull(user);
            assertEquals(role, user.getRole());
        }

        static Stream<Arguments> provideValidRoles() {
            return Stream.of(
                    Arguments.of(Role.USER),
                    Arguments.of(Role.ADMIN)
            );
        }

        @Test
        @DisplayName("Doit rejeter un role null")
        void shouldRejectNullRole() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new User(VALID_EMAIL, VALID_PASSWORD, null)
            );
            assertEquals("role must not be null", exception.getMessage());
        }
    }

    // ========== Tests de la méthode canAccessAdminArea() ==========
    @Nested
    @DisplayName("Tests de canAccessAdminArea()")
    class CanAccessAdminAreaTests {

        @Test
        @DisplayName("Doit retourner true si le rôle est ADMIN")
        void shouldReturnTrueForAdmin() {
            User adminUser = new User(VALID_EMAIL, VALID_PASSWORD, Role.ADMIN);
            assertTrue(adminUser.canAccessAdminArea());
        }

        @Test
        @DisplayName("Doit retourner false si le rôle est USER")
        void shouldReturnFalseForUser() {
            User normalUser = new User(VALID_EMAIL, VALID_PASSWORD, Role.USER);
            assertFalse(normalUser.canAccessAdminArea());
        }
    }

    // ========== Tests de création d'un User valide ==========
    @Nested
    @DisplayName("Tests de création d'un User valide")
    class UserCreationTests {

        @Test
        @DisplayName("Doit créer un utilisateur valide avec tous les champs corrects")
        void shouldCreateValidUser() {
            User user = new User(VALID_EMAIL, VALID_PASSWORD, VALID_ROLE);

            assertAll(
                    () -> assertEquals(VALID_EMAIL, user.getEmail()),
                    () -> assertEquals(VALID_PASSWORD, user.getPassword()),
                    () -> assertEquals(VALID_ROLE, user.getRole())
            );
        }
    }

    // ========== Tests du UserService ==========
    @Nested
    @DisplayName("Tests du UserService")
    class UserServiceTests {

        private final UserService userService = new UserService();

        @Test
        @DisplayName("register() doit créer et retourner un utilisateur valide")
        void shouldRegisterValidUser() {
            User user = userService.register(VALID_EMAIL, VALID_PASSWORD, VALID_ROLE);

            assertNotNull(user);
            assertAll(
                    () -> assertEquals(VALID_EMAIL, user.getEmail()),
                    () -> assertEquals(VALID_PASSWORD, user.getPassword()),
                    () -> assertEquals(VALID_ROLE, user.getRole())
            );
        }

        @Test
        @DisplayName("register() doit propager l'exception pour un email invalide")
        void shouldPropagateExceptionForInvalidEmail() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register("invalid-email", VALID_PASSWORD, VALID_ROLE)
            );
            assertEquals("email must be valid", exception.getMessage());
        }

        @Test
        @DisplayName("register() doit propager l'exception pour un password faible")
        void shouldPropagateExceptionForWeakPassword() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(VALID_EMAIL, "weak", VALID_ROLE)
            );
            assertEquals("password must be strong", exception.getMessage());
        }

        @Test
        @DisplayName("register() doit propager l'exception pour un role null")
        void shouldPropagateExceptionForNullRole() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.register(VALID_EMAIL, VALID_PASSWORD, null)
            );
            assertEquals("role must not be null", exception.getMessage());
        }
    }
}