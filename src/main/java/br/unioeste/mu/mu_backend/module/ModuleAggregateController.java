package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules/full")
@Tag(name = "Modules (aggregate)")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.POST}
)
public class ModuleAggregateController {

    private final ModuleAggregateService moduleAggregateService;

    public ModuleAggregateController(ModuleAggregateService moduleAggregateService) {
        this.moduleAggregateService = moduleAggregateService;
    }

    @PostMapping
    @Operation(summary = "Create module with lessons, exercises, and extra materials in a single request")
    public ModuleAggregateResponse create(@Valid @RequestBody ModuleAggregateRequest request) {
        return moduleAggregateService.createFullModule(request);
    }
}
