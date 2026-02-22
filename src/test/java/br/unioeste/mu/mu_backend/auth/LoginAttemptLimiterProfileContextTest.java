package br.unioeste.mu.mu_backend.auth;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LoginAttemptLimiterProfileContextTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestContextConfiguration.class);

    @ParameterizedTest
    @CsvSource({
            "test,LocalLoginAttemptLimiter",
            "local,LocalLoginAttemptLimiter",
            "dev,LocalLoginAttemptLimiter",
            "prod,RedisLoginAttemptLimiter"
    })
    void shouldLoadSingleLoginAttemptLimiterPerProfile(String profile, String expectedLimiterClassName) {
        contextRunner
                .withPropertyValues("spring.profiles.active=" + profile)
                .run(context -> {
                    assertThat(context).hasSingleBean(LoginAttemptLimiter.class);
                    assertThat(context).hasSingleBean(AuthController.class);
                    assertThat(context.getBean(LoginAttemptLimiter.class).getClass().getSimpleName())
                            .isEqualTo(expectedLimiterClassName);
                });
    }

    @Configuration
    @Import({AuthController.class, LocalLoginAttemptLimiter.class, RedisLoginAttemptLimiter.class})
    static class TestContextConfiguration {

        @Bean
        AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }

        @Bean
        JwtService jwtService() {
            return mock(JwtService.class);
        }

        @Bean
        StringRedisTemplate stringRedisTemplate() {
            return mock(StringRedisTemplate.class);
        }
    }
}
