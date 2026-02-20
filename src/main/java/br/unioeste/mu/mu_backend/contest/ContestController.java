package br.unioeste.mu.mu_backend.contest;

import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT}
)
public class ContestController {

    private final ContestRepository contestRepository;
    private final ContestTeamService contestTeamService;

    public ContestController(ContestRepository contestRepository, ContestTeamService contestTeamService) {
        this.contestRepository = contestRepository;
        this.contestTeamService = contestTeamService;
    }

    @GetMapping
    public List<ContestResponse> list() {
        return contestRepository.findAllByOrderByStartDateTimeDesc()
                .stream()
                .map(ContestResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ContestResponse get(@PathVariable Long id) {
        return contestRepository.findById(id)
                .map(ContestResponse::from)
                .orElseThrow(() -> new NotFoundException("Contest não encontrado para id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContestResponse create(@Valid @RequestBody ContestRequest request) {
        Contest contest = new Contest();
        request.applyTo(contest);
        return ContestResponse.from(contestRepository.save(contest));
    }

    @PutMapping("/{id}")
    public ContestResponse update(@PathVariable Long id, @Valid @RequestBody ContestRequest request) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contest não encontrado para id=" + id));
        request.applyTo(contest);
        return ContestResponse.from(contestRepository.save(contest));
    }

    @GetMapping("/{contestId}/teams")
    public List<ContestTeamResponse> listTeams(@PathVariable Long contestId) {
        return contestTeamService.listTeams(contestId);
    }

    @PostMapping("/{contestId}/teams")
    @ResponseStatus(HttpStatus.CREATED)
    public ContestTeamResponse registerTeam(@PathVariable Long contestId,
                                            @Valid @RequestBody ContestTeamRegistrationRequest request) {
        return contestTeamService.registerTeam(contestId, request);
    }
}
