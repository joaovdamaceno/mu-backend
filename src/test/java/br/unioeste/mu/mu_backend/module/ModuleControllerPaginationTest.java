package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ModuleController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ModuleControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleRepository moduleRepository;

    @MockBean
    private ModuleAggregateService moduleAggregateService;


    @Test
    void shouldRejectModulePayloadWithNonPermittedFields() throws Exception {
        String payload = """
                {
                  "title": "Módulo 1",
                  "notes": "Notas",
                  "published": true,
                  "id": 100,
                  "createdAt": "2024-01-01T00:00:00",
                  "lessons": []
                }
                """;

        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[?(@.field=='payloadValid')]").isNotEmpty());

        verifyNoInteractions(moduleRepository);
    }


    @Test
    void shouldReturnCreatedWhenCreatingLegacyModule() throws Exception {
        Module module = new Module();
        module.setTitle("Módulo 1");
        module.setNotes("Notas");
        module.setPublished(true);

        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        String payload = """
                {
                  "title": "Módulo 1",
                  "notes": "Notas",
                  "published": true
                }
                """;

        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Módulo 1"));
    }

    @Test
    void shouldApplyPaginationAndSortingForModulesList() throws Exception {
        Module module = new Module();
        module.setTitle("Módulo 1");

        PageRequest pageRequest = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id"));

        when(moduleRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(module), pageRequest, 11));

        mockMvc.perform(get("/api/modules").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Módulo 1"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(moduleRepository).findAll(PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "id")));
    }

    @Test
    void shouldListFullModulesWithNestedContent() throws Exception {
        Module module = new Module();
        module.setTitle("Módulo completo");
        module.setNotes("Notas gerais");
        module.setPublished(true);

        ModuleAggregateResponse response = new ModuleAggregateResponse(module, List.of(), List.of(), List.of());
        when(moduleAggregateService.listAllFullModules()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/modules/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].module.title").value("Módulo completo"))
                .andExpect(jsonPath("$[0].lessons").isArray())
                .andExpect(jsonPath("$[0].exercises").isArray())
                .andExpect(jsonPath("$[0].extraMaterials").isArray());
    }

    @Test
    void shouldRejectModulesListWhenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/modules").param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moduleRepository);
    }

    @Test
    void shouldRejectModulesListWhenSizeIsLessThanOne() throws Exception {
        mockMvc.perform(get("/api/modules").param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(moduleRepository);
    }
}
