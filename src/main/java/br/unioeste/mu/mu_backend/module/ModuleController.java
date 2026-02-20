package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleRepository repository;
    private final ModuleAggregateService moduleAggregateService;

    public ModuleController(ModuleRepository repository, ModuleAggregateService moduleAggregateService) {
        this.repository = repository;
        this.moduleAggregateService = moduleAggregateService;
    }

    @GetMapping
    public Page<ModuleResponse> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return repository.findAll(PageRequest.of(page, size))
                .map(ModuleResponse::from);
    }

    @GetMapping("/{id}")
    public ModuleResponse get(@PathVariable Long id) {
        return repository.findById(id)
                .map(ModuleResponse::from)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + id));
    }

    @PostMapping
    @Operation(summary = "Create module (legacy endpoint). Prefer POST /api/modules/full for the form flow")
    public ModuleResponse create(@Valid @RequestBody Module module) {
        return ModuleResponse.from(repository.save(module));
    }

    @PostMapping("/full")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create module with lessons, exercises, and extra materials in a single request")
    public ModuleAggregateResponse createFull(@Valid @RequestBody ModuleAggregateRequest request) {
        return moduleAggregateService.createFullModule(request);
    }

    @PutMapping("/{id}")
    public ModuleResponse update(@PathVariable Long id, @Valid @RequestBody Module module) {
        Module existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + id));
        existing.setTitle(module.getTitle());
        existing.setNotes(module.getNotes());
        existing.setPublished(module.isPublished());
        return ModuleResponse.from(repository.save(existing));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Module module = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + id));
        repository.delete(module);
    }
}
