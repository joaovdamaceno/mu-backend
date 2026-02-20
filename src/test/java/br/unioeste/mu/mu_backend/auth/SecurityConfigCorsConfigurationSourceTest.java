package br.unioeste.mu.mu_backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityConfigCorsConfigurationSourceTest {

    @Test
    void shouldLoadSingleAllowedOriginFromConfiguration() {
        CorsConfiguration configuration = corsConfigurationForOrigins("https://frontend.example.com");

        assertEquals(List.of("https://frontend.example.com"), configuration.getAllowedOrigins());
    }

    @Test
    void shouldLoadTwoAllowedOriginsFromConfiguration() {
        CorsConfiguration configuration = corsConfigurationForOrigins("https://app.example.com", "https://admin.example.com");

        assertEquals(List.of("https://app.example.com", "https://admin.example.com"), configuration.getAllowedOrigins());
    }

    @Test
    void shouldLoadAllAllowedOriginsFromConfigurationWithoutTruncation() {
        CorsConfiguration configuration = corsConfigurationForOrigins(
                "https://one.example.com",
                "https://two.example.com",
                "https://three.example.com"
        );

        assertEquals(
                List.of("https://one.example.com", "https://two.example.com", "https://three.example.com"),
                configuration.getAllowedOrigins()
        );
    }

    private CorsConfiguration corsConfigurationForOrigins(String... origins) {
        MockEnvironment environment = new MockEnvironment();
        for (int i = 0; i < origins.length; i++) {
            environment.setProperty("app.cors.allowed-origins[" + i + "]", origins[i]);
        }

        SecurityConfig securityConfig = new SecurityConfig(new NoOpJwtAuthFilter(), environment, new ObjectMapper());
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        return source.getCorsConfiguration(new MockHttpServletRequest("OPTIONS", "/api/posts"));
    }

    private static class NoOpJwtAuthFilter extends JwtAuthFilter {

        NoOpJwtAuthFilter() {
            super(null, null);
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(request, response);
        }
    }
}
