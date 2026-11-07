package com.example.template.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTests {

    private final SecurityConfig config = new SecurityConfig();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankUsername(String username) {
        var failure = assertThrows(IllegalStateException.class,
                () -> config.userDetailsService(username, "a-secret", encoder));
        assertTrue(failure.getMessage().contains("app.security.username"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingOrBlankPassword(String password) {
        var failure = assertThrows(IllegalStateException.class,
                () -> config.userDetailsService("template-user", password, encoder));
        assertTrue(failure.getMessage().contains("app.security.password"));
    }

    @Test
    void storesBcryptHashInsteadOfPlainPassword() throws Exception {
        UserDetailsService users = config.userDetailsService("template-user", "plain-secret", encoder);

        UserDetails user = users.loadUserByUsername("template-user");

        assertNotEquals("plain-secret", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2"), "password must be stored as a BCrypt hash");
        assertTrue(encoder.matches("plain-secret", user.getPassword()));
        assertEquals(0, user.getAuthorities().size());
    }

    @Test
    void unknownUsernameIsNotFound() {
        UserDetailsService users = config.userDetailsService("template-user", "plain-secret", encoder);

        assertThrows(UsernameNotFoundException.class, () -> users.loadUserByUsername("someone-else"));
    }
}
