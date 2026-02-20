package br.unioeste.mu.mu_backend.contest;

import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContestTeamServiceTest {

    @Mock
    private ContestRepository contestRepository;

    @Mock
    private ContestTeamRepository contestTeamRepository;

    @InjectMocks
    private ContestTeamService contestTeamService;

    @Test
    void shouldThrowConflictWhenTeamNameAlreadyExistsInContestIgnoringCase() {
        Long contestId = 1L;
        ContestTeamRegistrationRequest request = validTeamBasedRequest();
        request.setTeamName("  Team Rocket  ");

        Contest contest = new Contest();
        contest.setTeamBased(true);

        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(contestTeamRepository.existsByContestIdAndTeamNameIgnoreCase(contestId, "Team Rocket")).thenReturn(true);

        assertThatThrownBy(() -> contestTeamService.registerTeam(contestId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe um time com este nome neste contest");

        verify(contestTeamRepository, never()).save(any(ContestTeam.class));
    }

    @Test
    void shouldTrimTeamNameAndPersistWhenNameIsAvailable() {
        Long contestId = 2L;
        ContestTeamRegistrationRequest request = validTeamBasedRequest();
        request.setTeamName("  Time Alpha  ");

        Contest contest = new Contest();
        contest.setTeamBased(true);

        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(contestTeamRepository.existsByContestIdAndTeamNameIgnoreCase(contestId, "Time Alpha")).thenReturn(false);
        when(contestTeamRepository.save(any(ContestTeam.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contestTeamService.registerTeam(contestId, request);

        ArgumentCaptor<ContestTeam> teamCaptor = ArgumentCaptor.forClass(ContestTeam.class);
        verify(contestTeamRepository).save(teamCaptor.capture());
        verify(contestTeamRepository).existsByContestIdAndTeamNameIgnoreCase(eq(contestId), eq("Time Alpha"));

        assertThat(teamCaptor.getValue().getTeamName()).isEqualTo("Time Alpha");
    }

    @Test
    void shouldThrowConflictWhenRepositorySaveFailsWithUniqueConstraintViolation() {
        Long contestId = 3L;
        ContestTeamRegistrationRequest request = validTeamBasedRequest();
        request.setTeamName("  Time Beta  ");

        Contest contest = new Contest();
        contest.setTeamBased(true);

        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(contestTeamRepository.existsByContestIdAndTeamNameIgnoreCase(contestId, "Time Beta")).thenReturn(false);
        when(contestTeamRepository.save(any(ContestTeam.class))).thenThrow(
                new DataIntegrityViolationException(
                        "duplicate key value violates unique constraint",
                        new RuntimeException("contest_teams(contest_id, lower(team_name))")
                )
        );

        assertThatThrownBy(() -> contestTeamService.registerTeam(contestId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe um time com este nome neste contest");
    }

    @Test
    void shouldAllowSingleCompetitorForIndividualContest() {
        Long contestId = 4L;
        ContestTeamRegistrationRequest request = new ContestTeamRegistrationRequest();
        request.setTeamName("Solo");
        request.setCompetitor1Name("Competidor Solo");

        Contest contest = new Contest();
        contest.setTeamBased(false);

        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));
        when(contestTeamRepository.existsByContestIdAndTeamNameIgnoreCase(contestId, "Solo")).thenReturn(false);
        when(contestTeamRepository.save(any(ContestTeam.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contestTeamService.registerTeam(contestId, request);

        ArgumentCaptor<ContestTeam> teamCaptor = ArgumentCaptor.forClass(ContestTeam.class);
        verify(contestTeamRepository).save(teamCaptor.capture());

        ContestTeam saved = teamCaptor.getValue();
        assertThat(saved.getCoachName()).isNull();
        assertThat(saved.getMembers()).hasSize(1);
        assertThat(saved.getMembers().get(0).getMemberIndex()).isEqualTo(1);
        assertThat(saved.getMembers().get(0).getMemberName()).isEqualTo("Competidor Solo");
    }

    @Test
    void shouldRequireCompetitorTwoAndThreeForTeamBasedContest() {
        Long contestId = 5L;
        ContestTeamRegistrationRequest request = new ContestTeamRegistrationRequest();
        request.setTeamName("Equipe incompleta");
        request.setCompetitor1Name("Competidor 1");

        Contest contest = new Contest();
        contest.setTeamBased(true);

        when(contestRepository.findById(contestId)).thenReturn(Optional.of(contest));

        assertThatThrownBy(() -> contestTeamService.registerTeam(contestId, request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Competidor 2 é obrigatório");

        verify(contestTeamRepository, never()).save(any(ContestTeam.class));
    }

    private ContestTeamRegistrationRequest validTeamBasedRequest() {
        ContestTeamRegistrationRequest request = new ContestTeamRegistrationRequest();
        request.setTeamName("Equipe");
        request.setCompetitor1Name("Competidor 1");
        request.setCompetitor2Name("Competidor 2");
        request.setCompetitor3Name("Competidor 3");
        return request;
    }
}
