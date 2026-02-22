package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.registration.Registration;
import br.unioeste.mu.mu_backend.registration.RegistrationController;
import br.unioeste.mu.mu_backend.registration.RegistrationRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@Import({SecurityConfig.class, RegistrationEndpointSecurityWebMvcTest.SecurityTestConfig.class})
class RegistrationEndpointSecurityWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationRepository registrationRepository;

    @Test
    void shouldAllowAnonymousPostToRegistrations() throws Exception {
        Registration registration = new Registration();
        registration.setName("Maria");
        registration.setEmail("maria@example.com");
        registration.setCampus("Campus A");
        registration.setCourse("Curso A");
        registration.setSemester("1");
        registration.setHowDidYouHear("Instagram");

        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        mockMvc.perform(post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldKeepOtherPostEndpointsProtectedForAnonymousUsers() throws Exception {
        mockMvc.perform(post("/api/registrations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isUnauthorized());
    }

    private String validPayload() {
        return """
                {
                  "name": "Maria",
                  "email": "maria@example.com",
                  "campus": "Campus A",
                  "course": "Curso A",
                  "semester": "1",
                  "howDidYouHear": "Instagram"
                }
                """;
    }

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        JwtAuthFilter jwtAuthFilter() {
            return new JwtAuthFilter(null, null) {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }
}
