package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.post.PostController;
import br.unioeste.mu.mu_backend.post.PostRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@Import({SecurityConfig.class, CorsPreflightWebMvcTest.SecurityTestConfig.class})
@TestPropertySource(properties = {
        "app.cors.allowed-origins[0]=https://allowed.example.com",
        "app.cors.allowed-origins[1]=https://also-allowed.example.com"
})
class CorsPreflightWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;


    @Test
    void shouldAllowPreflightForConfiguredOrigin() throws Exception {
        mockMvc.perform(options("/api/posts")
                        .header(ORIGIN, "https://allowed.example.com")
                        .header(ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "https://allowed.example.com"));
    }

    @Test
    void shouldRejectPreflightForNonConfiguredOrigin() throws Exception {
        mockMvc.perform(options("/api/posts")
                        .header(ORIGIN, "https://blocked.example.com")
                        .header(ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void shouldAllowSimpleGetForConfiguredOrigin() throws Exception {
        when(postRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/posts")
                        .header(ORIGIN, "https://also-allowed.example.com"))
                .andExpect(status().isOk())
                .andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "https://also-allowed.example.com"));
    }

    @Test
    void shouldNotExposeCorsHeadersForNonConfiguredOriginInSimpleGet() throws Exception {
        when(postRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/posts")
                        .header(ORIGIN, "https://blocked.example.com"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
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
