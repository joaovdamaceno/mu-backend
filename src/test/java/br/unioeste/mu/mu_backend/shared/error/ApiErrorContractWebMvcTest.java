package br.unioeste.mu.mu_backend.shared.error;

import br.unioeste.mu.mu_backend.auth.JwtAuthFilter;
import br.unioeste.mu.mu_backend.auth.SecurityConfig;
import br.unioeste.mu.mu_backend.exercise.ExerciseController;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.post.PostController;
import br.unioeste.mu.mu_backend.post.PostRepository;
import br.unioeste.mu.mu_backend.post.PostSectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class, ExerciseController.class})
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ApiErrorContractWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostSectionRepository postSectionRepository;

    @MockBean
    private ExerciseRepository exerciseRepository;

    @MockBean
    private ModuleRepository moduleRepository;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUnifiedContractForMethodArgumentNotValidOnPostEndpoint() throws Exception {
        String payload = """
                {
                  "title": "",
                  "slug": "INVALID SLUG",
                  "authorName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Falha de validação dos dados enviados."))
                .andExpect(jsonPath("$.path").value("/api/posts"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[?(@.field=='title')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='slug')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='authorName')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='status')]").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUnifiedContractForInvalidEnumValue() throws Exception {
        String payload = """
                {
                  "title": "Two Sum",
                  "ojName": "LeetCode",
                  "ojUrl": "https://leetcode.com/problems/two-sum",
                  "difficulty": "VERY_HARD",
                  "tags": ["array"]
                }
                """;

        mockMvc.perform(post("/api/modules/1/lessons/1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_JSON"))
                .andExpect(jsonPath("$.message").value("Corpo da requisição inválido."))
                .andExpect(jsonPath("$.path").value("/api/modules/1/lessons/1/exercises"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0].field").value("difficulty"));
    }

    @Test
    void shouldReturnUnifiedContractForNotFoundException() throws Exception {
        given(postRepository.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Post não encontrado para id=99"))
                .andExpect(jsonPath("$.path").value("/api/posts/99"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldReturnUnauthorizedWhenPostEndpointIsAccessedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPostPayload()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Não autorizado."))
                .andExpect(jsonPath("$.path").value("/api/posts"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenAuthenticatedUserHasNoAdminRole() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPostPayload()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Acesso negado."))
                .andExpect(jsonPath("$.path").value("/api/posts"))
                .andExpect(jsonPath("$.details").isArray());
    }

    private String validPostPayload() {
        return """
                {
                  "title": "Título válido",
                  "slug": "titulo-valido",
                  "authorName": "Autor",
                  "status": "PUBLISHED"
                }
                """;
    }
}
