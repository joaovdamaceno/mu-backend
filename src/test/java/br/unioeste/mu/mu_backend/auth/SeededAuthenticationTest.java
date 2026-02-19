package br.unioeste.mu.mu_backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeededAuthenticationTest {

    private static final String SEEDED_BCRYPT_HASH = "$2a$10$abcdefghijklmnopqrstuu5Lo0g67CiD3M4RpN1BmBb4Crp5w7dbK";

    @Test
    void seededAdminPasswordIsAcceptedByApplicationEncoder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        assertTrue(
                passwordEncoder.matches("password", SEEDED_BCRYPT_HASH),
                "Seeded admin password hash must authenticate with BCryptPasswordEncoder"
        );
    }
    @Test
    void seedScriptsContainAdminWithValidatedHash() throws IOException {
        String migrationSeed = Files.readString(Path.of("src/main/resources/db/migration/V11__seed_default_users.sql"));
        String populateSeed = Files.readString(Path.of("populate.sql"));

        assertTrue(migrationSeed.contains("admin"));
        assertTrue(migrationSeed.contains(SEEDED_BCRYPT_HASH));
        assertFalse(populateSeed.contains("INSERT INTO users"));
        assertFalse(populateSeed.contains(SEEDED_BCRYPT_HASH));
    }

}
