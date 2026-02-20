package br.unioeste.mu.mu_backend.contest;

import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContestTeamService {

    private final ContestRepository contestRepository;
    private final ContestTeamRepository contestTeamRepository;

    public ContestTeamService(ContestRepository contestRepository, ContestTeamRepository contestTeamRepository) {
        this.contestRepository = contestRepository;
        this.contestTeamRepository = contestTeamRepository;
    }

    @Transactional
    public ContestTeamResponse registerTeam(Long contestId, ContestTeamRegistrationRequest request) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Contest não encontrado para id=" + contestId));

        if (!contest.isTeamBased()) {
            throw new BusinessValidationException("Este contest é individual e não aceita inscrição de times");
        }

        ContestTeam team = new ContestTeam();
        team.setContest(contest);
        team.setTeamName(request.getTeamName().trim());
        team.setCoachName(trimToNull(request.getCoachName()));
        team.setInstitution(trimToNull(request.getInstitution()));
        team.setReserveName(trimToNull(request.getReserveName()));
        team.setCafeComLeite(request.isCafeComLeite());

        team.addMember(newMember(1, request.getCompetitor1Name()));
        team.addMember(newMember(2, request.getCompetitor2Name()));
        team.addMember(newMember(3, request.getCompetitor3Name()));

        ContestTeam saved = contestTeamRepository.save(team);
        return ContestTeamResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ContestTeamResponse> listTeams(Long contestId) {
        if (!contestRepository.existsById(contestId)) {
            throw new NotFoundException("Contest não encontrado para id=" + contestId);
        }
        return contestTeamRepository.findAllByContestIdWithMembersOrderByCreatedAtAsc(contestId)
                .stream()
                .map(ContestTeamResponse::from)
                .toList();
    }

    private ContestTeamMember newMember(int index, String name) {
        ContestTeamMember member = new ContestTeamMember();
        member.setMemberIndex(index);
        member.setMemberName(name.trim());
        return member;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
