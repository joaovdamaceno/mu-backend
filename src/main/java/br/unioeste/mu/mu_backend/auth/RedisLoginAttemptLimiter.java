package br.unioeste.mu.mu_backend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Component
@Profile("!dev & !local")
public class RedisLoginAttemptLimiter implements LoginAttemptLimiter {

    private static final Logger log = LoggerFactory.getLogger(RedisLoginAttemptLimiter.class);

    private static final byte[] INCR_WITH_EXPIRE_SCRIPT = """
            local value = redis.call('INCR', KEYS[1])
            if value == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            return value
            """.getBytes(StandardCharsets.UTF_8);

    private final StringRedisTemplate redisTemplate;
    private final int maxAttempts;
    private final long windowSeconds;

    public RedisLoginAttemptLimiter(StringRedisTemplate redisTemplate,
                                    @Value("${app.auth.login.max-attempts:5}") int maxAttempts,
                                    @Value("${app.auth.login.window-seconds:300}") long windowSeconds) {
        this.redisTemplate = redisTemplate;
        this.maxAttempts = maxAttempts;
        this.windowSeconds = windowSeconds;
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

        String currentValue = redisTemplate.opsForValue().get(key);
        if (currentValue == null) {
            return false;
        }
        return Long.parseLong(currentValue) >= maxAttempts;
    }

    private void increment(String key) {
        if (key == null) {
            return;
        }

        try {
            redisTemplate.execute(
                    connection -> connection.scriptingCommands().eval(
                            INCR_WITH_EXPIRE_SCRIPT,
                            ReturnType.INTEGER,
                            1,
                            key.getBytes(StandardCharsets.UTF_8),
                            String.valueOf(windowSeconds).getBytes(StandardCharsets.UTF_8)
                    ),
                    true
            );
        } catch (DataAccessException ex) {
            throw new IllegalStateException("Erro ao registrar tentativa de login no Redis", ex);
        }
    }

    private void clear(String key) {
        if (key == null) {
            return;
        }
        redisTemplate.delete(List.of(key));
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
}
