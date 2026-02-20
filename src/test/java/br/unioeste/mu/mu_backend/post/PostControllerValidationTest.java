package br.unioeste.mu.mu_backend.post;

import br.unioeste.mu.mu_backend.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    void shouldRejectPostPayloadWithNonPermittedFields() throws Exception {
        String payload = """
                {
                  "title": "Post válido",
                  "slug": "post-valido",
                  "authorName": "Autor",
                  "status": "PUBLISHED",
                  "id": 99,
                  "createdAt": "2024-01-01T00:00:00",
                  "sections": [
                    {
                      "position": 0,
                      "text": "Texto",
                      "post": {"id": 1}
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[?(@.field=='payloadValid')]").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='sections[0].payloadValid')]").isNotEmpty());

        verifyNoInteractions(postRepository);
    }

    @Test
    void shouldApplyPaginationAndSortingForPostsList() throws Exception {
        Post post = new Post();
        post.setTitle("Post A");
        post.setSlug("post-a");
        post.setAuthorName("Autor");
        post.setStatus("PUBLISHED");

        PostSection section1 = new PostSection();
        section1.setPosition(2);
        section1.setText("Segundo");

        PostSection section2 = new PostSection();
        section2.setPosition(1);
        section2.setText("Primeiro");

        post.setSections(List.of(section1, section2));

        PageRequest pageRequest = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "updatedAt", "id"));

        when(postRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(post), pageRequest, 15));

        mockMvc.perform(get("/api/posts").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Post A"))
                .andExpect(jsonPath("$.content[0].sections[0].position").value(1))
                .andExpect(jsonPath("$.content[0].sections[1].position").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(postRepository).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "updatedAt", "id")));
    }

    @Test
    void shouldRejectPostsListWhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/posts").param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postRepository);
    }

    @Test
    void shouldRejectPostsListWhenSizeIsLessThanOne() throws Exception {
        mockMvc.perform(get("/api/posts").param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(postRepository);
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
    void shouldReturnOrderedSectionsOnGetById() throws Exception {
        long postId = 10L;
        Post post = new Post();
        post.setTitle("Post");
        post.setSlug("post");
        post.setAuthorName("Autor");
        post.setStatus("PUBLISHED");

        PostSection section1 = new PostSection();
        section1.setPosition(3);
        section1.setText("Terceiro");

        PostSection section2 = new PostSection();
        section2.setPosition(1);
        section2.setText("Primeiro");

        post.setSections(List.of(section1, section2));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections[0].position").value(1))
                .andExpect(jsonPath("$.sections[1].position").value(3));
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
