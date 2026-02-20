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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("local")
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class AuthControllerClientIpWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LoginAttemptLimiter loginAttemptLimiter;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void shouldUseRemoteAddrWhenForwardHeadersAreAbsent() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        "admin",
                        "N/A",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ));
        when(jwtService.generateToken("admin", "ROLE_ADMIN")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "correct"))
                        .with(req -> {
                            req.setRemoteAddr("198.51.100.24");
                            return req;
                        }))
                .andExpect(status().isOk());

        verify(loginAttemptLimiter).checkAllowed(eq("198.51.100.24"), eq("admin"));
        verify(loginAttemptLimiter).registerSuccess(eq("198.51.100.24"), eq("admin"));
    }

    @Test
    void shouldRelyOnNormalizedRemoteAddrEvenWhenForwardHeaderIsPresent() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("invalid"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload("admin", "wrong"))
                        .header("X-Forwarded-For", "203.0.113.10, 10.0.0.8")
                        .with(req -> {
                            req.setRemoteAddr("203.0.113.10");
                            return req;
                        }))
                .andExpect(status().isUnauthorized());

        verify(loginAttemptLimiter).checkAllowed(eq("203.0.113.10"), eq("admin"));
        verify(loginAttemptLimiter).registerFailure(eq("203.0.113.10"), eq("admin"));
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
