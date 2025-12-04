package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/materials")
@CrossOrigin
public class ExtraMaterialController {

    private final ExtraMaterialRepository extraMaterialRepository;
    private final ModuleRepository moduleRepository;

    public ExtraMaterialController(ExtraMaterialRepository extraMaterialRepository, ModuleRepository moduleRepository) {
        this.extraMaterialRepository = extraMaterialRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<ExtraMaterial> list(@PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return extraMaterialRepository.findByModule(module);
    }

    @PostMapping
    public ExtraMaterial create(@PathVariable Long moduleId, @Valid @RequestBody ExtraMaterial material) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        material.setModule(module);
        return extraMaterialRepository.save(material);
    }
}
