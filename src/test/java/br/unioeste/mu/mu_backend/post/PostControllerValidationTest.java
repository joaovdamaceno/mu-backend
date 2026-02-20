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

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.details[?(@.field=='title' && @.message=='Título é obrigatório' && @.rejectedValue=='')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='slug' && @.message=='Slug deve conter apenas letras minúsculas, números e hífens' && @.rejectedValue=='INVALID SLUG')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='authorName' && @.message=='Autor é obrigatório' && @.rejectedValue=='')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='status' && @.message=='Status é obrigatório' && @.rejectedValue=='')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='sections[0].position')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='sections[0].contentValid')]").isNotEmpty());
    }

    @Test
    void shouldReturnNotFoundContractWhenPostDoesNotExistOnGet() throws Exception {
        long postId = 99L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Post não encontrado para id=" + postId))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldReturnNotFoundContractWhenPostDoesNotExistOnDelete() throws Exception {
        long postId = 77L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/posts/{id}", postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Post não encontrado para id=" + postId))
                .andExpect(jsonPath("$.path").value("/api/posts/" + postId))
                .andExpect(jsonPath("$.details").isArray());
    }

}
