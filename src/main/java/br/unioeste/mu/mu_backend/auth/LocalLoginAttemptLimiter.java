package br.unioeste.mu.mu_backend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile({"dev", "local", "test"})
public class LocalLoginAttemptLimiter implements LoginAttemptLimiter {

    private static final Logger log = LoggerFactory.getLogger(LocalLoginAttemptLimiter.class);

    private final int maxAttempts;
    private final long windowSeconds;
    private final Clock clock;
    private final ConcurrentMap<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

    public LocalLoginAttemptLimiter(@Value("${app.auth.login.max-attempts:5}") int maxAttempts,
                                    @Value("${app.auth.login.window-seconds:300}") long windowSeconds) {
        this(maxAttempts, windowSeconds, Clock.systemUTC());
    }

    LocalLoginAttemptLimiter(int maxAttempts, long windowSeconds, Clock clock) {
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
        this.clock = clock;
    }

    @Override
    public void checkAllowed(String ipAddress, String username) {
        if (isLimited(ipKey(ipAddress)) || isLimited(userKey(ipAddress, username))) {
            throw new LoginRateLimitExceededException("Muitas tentativas de login. Tente novamente mais tarde.");
        }
    }

    @Override
    public void registerFailure(String ipAddress, String username) {
        increment(ipKey(ipAddress));
        increment(userKey(ipAddress, username));
        log.warn("Falha de autenticação registrada para ip={} username={}", ipAddress, normalizeUsername(username));
    }

    @Override
    public void registerSuccess(String ipAddress, String username) {
        clear(ipKey(ipAddress));
        clear(userKey(ipAddress, username));
        log.info("Autenticação válida, contadores limpos para ip={} username={}", ipAddress, normalizeUsername(username));
    }

    private boolean isLimited(String key) {
        if (key == null) {
            return false;
        }
        return attempts.computeIfAbsent(key, ignored -> new AttemptWindow()).isLimited(nowEpochSecond(), windowSeconds, maxAttempts);
    }

    private void increment(String key) {
        if (key == null) {
            return;
        }
        attempts.computeIfAbsent(key, ignored -> new AttemptWindow()).increment(nowEpochSecond(), windowSeconds);
    }

    private void clear(String key) {
        if (key == null) {
            return;
        }
        attempts.remove(key);
    }

    private String ipKey(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return null;
        }
        return "ip:" + ipAddress;
    }

    private String userKey(String ipAddress, String username) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return null;
        }
        String normalizedUsername = normalizeUsername(username);
        if (normalizedUsername == null) {
            return null;
        }
        return "ip-user:" + ipAddress + ":" + normalizedUsername;
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private long nowEpochSecond() {
        return Instant.now(clock).getEpochSecond();
    }

    private static class AttemptWindow {
        private final Deque<Long> timestamps = new ArrayDeque<>();

        synchronized void increment(long nowEpochSecond, long windowSeconds) {
            prune(nowEpochSecond, windowSeconds);
            timestamps.addLast(nowEpochSecond);
        }

        synchronized boolean isLimited(long nowEpochSecond, long windowSeconds, int maxAttempts) {
            prune(nowEpochSecond, windowSeconds);
            return timestamps.size() >= maxAttempts;
        }

        private void prune(long nowEpochSecond, long windowSeconds) {
            long threshold = nowEpochSecond - windowSeconds;
            while (!timestamps.isEmpty() && timestamps.peekFirst() <= threshold) {
                timestamps.removeFirst();
            }
        }
    }
}
