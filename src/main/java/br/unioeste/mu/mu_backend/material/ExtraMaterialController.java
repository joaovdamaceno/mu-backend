package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/materials")
public class ExtraMaterialController {

    private final ExtraMaterialRepository extraMaterialRepository;
    private final ModuleRepository moduleRepository;

    public ExtraMaterialController(ExtraMaterialRepository extraMaterialRepository, ModuleRepository moduleRepository) {
        this.extraMaterialRepository = extraMaterialRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<ExtraMaterial> list(@PathVariable Long moduleId) {
        Module module = findModule(moduleId);
        return extraMaterialRepository.findByModule(module);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExtraMaterial create(@PathVariable Long moduleId, @Valid @RequestBody ExtraMaterialRequest request) {
        Module module = findModule(moduleId);
        ExtraMaterial material = new ExtraMaterial();
        request.applyTo(material, module);
        return extraMaterialRepository.save(material);
    }

    @PutMapping("/{materialId}")
    public ExtraMaterial update(@PathVariable Long moduleId,
                                @PathVariable Long materialId,
                                @Valid @RequestBody ExtraMaterialRequest request) {
        Module module = findModule(moduleId);

        ExtraMaterial material = extraMaterialRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material extra não encontrado para id=" + materialId));

        if (material.getModule() == null || !material.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Material extra id=" + materialId + " não pertence ao módulo id=" + moduleId);
        }

        request.applyTo(material, module);

        return extraMaterialRepository.save(material);
    }

    private Module findModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
    }
}
