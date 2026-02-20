package br.unioeste.mu.mu_backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "app.auth.login.max-attempts=2",
        "app.auth.login.window-seconds=1"
})
@ActiveProfiles("local")
class LocalLoginAttemptLimiterIntegrationTest {

    @Autowired
    private LoginAttemptLimiter loginAttemptLimiter;

    @Test
    void shouldBlockConsistentlyAcrossIpAndIpUserDimensions() {
        String ipAddress = "203.0.113.10";

        loginAttemptLimiter.registerFailure(ipAddress, "alice");
        loginAttemptLimiter.registerFailure(ipAddress, "bob");

        assertThrows(LoginRateLimitExceededException.class,
                () -> loginAttemptLimiter.checkAllowed(ipAddress, "charlie"));

        loginAttemptLimiter.registerSuccess(ipAddress, "charlie");

        loginAttemptLimiter.registerFailure(ipAddress, "alice");
        loginAttemptLimiter.registerFailure(ipAddress, "alice");

        assertThrows(LoginRateLimitExceededException.class,
                () -> loginAttemptLimiter.checkAllowed(ipAddress, "alice"));
    }

    @Test
    void shouldExpireAttemptsAfterWindow() throws InterruptedException {
        String ipAddress = "203.0.113.20";
        String username = "admin";

        loginAttemptLimiter.registerFailure(ipAddress, username);
        loginAttemptLimiter.registerFailure(ipAddress, username);

        assertThrows(LoginRateLimitExceededException.class,
                () -> loginAttemptLimiter.checkAllowed(ipAddress, username));

        Thread.sleep(1_200);

        assertDoesNotThrow(() -> loginAttemptLimiter.checkAllowed(ipAddress, username));
    }
}
