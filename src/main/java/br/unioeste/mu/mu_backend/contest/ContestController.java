package br.unioeste.mu.mu_backend.contest;

import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@Validated
public class ContestController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ContestRepository contestRepository;
    private final ContestTeamService contestTeamService;

    public ContestController(ContestRepository contestRepository, ContestTeamService contestTeamService) {
        this.contestRepository = contestRepository;
        this.contestTeamService = contestTeamService;
    }

    @GetMapping
    public Page<ContestResponse> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                      @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDateTime", "id"));
        return contestRepository.findAll(pageRequest)
                .map(ContestResponse::from);
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
