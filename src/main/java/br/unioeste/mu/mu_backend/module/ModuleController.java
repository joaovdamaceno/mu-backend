package br.unioeste.mu.mu_backend.module;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class ModuleController {

    private final ModuleRepository repository;

    public ModuleController(ModuleRepository repository) {
        this.repository = repository;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ModuleResponse create(@Valid @RequestBody Module module) {
        return ModuleResponse.from(repository.save(module));
    }

    @PutMapping("/{id}")
    public ModuleResponse update(@PathVariable Long id, @Valid @RequestBody Module module) {
        Module existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setTitle(module.getTitle());
        existing.setNotes(module.getNotes());
        existing.setPublished(module.isPublished());
        return ModuleResponse.from(repository.save(existing));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Module module = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.delete(module);
    }
}
