package br.unioeste.mu.mu_backend.module;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ModuleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModuleControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleRepository moduleRepository;

    @MockBean
    private ModuleAggregateService moduleAggregateService;

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
