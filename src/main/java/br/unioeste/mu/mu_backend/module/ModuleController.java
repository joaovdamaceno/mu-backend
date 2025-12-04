package br.unioeste.mu.mu_backend.module;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin
public class ModuleController {

    private final ModuleRepository repository;

    public ModuleController(ModuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Module> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Module get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping
    public Module create(@RequestBody Module module) {
        return repository.save(module);
    }

    @PutMapping("/{id}")
    public Module update(@PathVariable Long id, @RequestBody Module module) {
        Module existing = repository.findById(id).orElseThrow();
        existing.setTitle(module.getTitle());
        existing.setNotes(module.getNotes());
        existing.setPublished(module.isPublished());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
