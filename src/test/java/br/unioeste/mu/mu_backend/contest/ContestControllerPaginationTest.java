package br.unioeste.mu.mu_backend.contest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContestControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContestRepository contestRepository;

    @MockBean
    private ContestTeamService contestTeamService;

    @Test
    void shouldReturnPaginatedContractWithDefaultPagination() throws Exception {
        Contest contest = contest("Contest A", LocalDateTime.of(2025, 1, 10, 10, 0));
        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "startDateTime", "id"));

        when(contestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(contest), pageRequest, 1));

        mockMvc.perform(get("/api/contests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Contest A"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(contestRepository).findAll(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "startDateTime", "id")));
    }

    @Test
    void shouldApplyPaginationParamsFromQueryString() throws Exception {
        Contest contest = contest("Contest B", LocalDateTime.of(2025, 2, 15, 14, 0));
        PageRequest pageRequest = PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "startDateTime", "id"));

        when(contestRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(contest), pageRequest, 11));

        mockMvc.perform(get("/api/contests").param("page", "2").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(5));

        verify(contestRepository).findAll(PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC, "startDateTime", "id")));
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/contests").param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contestRepository);
    }

    @Test
    void shouldReturnBadRequestWhenPageSizeIsLessThanOne() throws Exception {
        mockMvc.perform(get("/api/contests").param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(contestRepository);
    }

    private Contest contest(String name, LocalDateTime startDateTime) {
        Contest contest = new Contest();
        contest.setName(name);
        contest.setDurationMinutes(120);
        contest.setStartDateTime(startDateTime);
        contest.setTeamBased(true);
        contest.setCodeforcesMirrorUrl("https://codeforces.com");
        return contest;
    }
}
