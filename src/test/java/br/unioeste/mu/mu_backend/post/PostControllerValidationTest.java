package br.unioeste.mu.mu_backend.post;

import br.unioeste.mu.mu_backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostSectionRepository sectionRepository;

    @Test
    void shouldReturnValidationDetailsByFieldForInvalidPostPayload() throws Exception {
        String payload = """
                {
                  "title": "",
                  "slug": "INVALID SLUG",
                  "authorName": "",
                  "status": "",
                  "sections": [
                    {
                      "position": -1,
                      "text": "",
                      "imageUrl": ""
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[?(@.field=='title')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='slug')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='authorName')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='status')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='sections[0].position')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='sections[0].contentValid')]").isNotEmpty());
    }
}
