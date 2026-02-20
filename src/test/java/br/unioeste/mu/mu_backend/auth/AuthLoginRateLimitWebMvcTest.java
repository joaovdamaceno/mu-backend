package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("local")
@Import({GlobalExceptionHandler.class, SecurityConfig.class, LocalLoginAttemptLimiter.class})
@TestPropertySource(properties = {
        "app.auth.login.max-attempts=2",
        "app.auth.login.window-seconds=1"
})
class AuthLoginRateLimitWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void shouldBlockAfterMaxFailedAttempts() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));

        performFailedLogin();
        performFailedLogin();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.code").value("LOGIN_RATE_LIMIT_EXCEEDED"));

        verify(authenticationManager, times(2)).authenticate(any());
    }

    @Test
    void shouldUnblockAfterWindowExpires() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));

        performFailedLogin();
        performFailedLogin();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isTooManyRequests());

        Thread.sleep(1_200);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldAllowValidCredentialsBeforeLimitAndResetCounters() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("invalid"))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        "admin",
                        "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ))
                .thenThrow(new BadCredentialsException("invalid"));
        when(jwtService.generateToken("admin", "ROLE_ADMIN")).thenReturn("jwt-token");

        performFailedLogin();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "correct"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verify(authenticationManager, times(3)).authenticate(any());
    }

    private void performFailedLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .with(req -> {
                            req.setRemoteAddr("10.0.0.1");
                            return req;
                        }))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private String loginPayload(String username, String password) {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
    }
}
