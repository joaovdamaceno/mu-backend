package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/exercises")
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;

    public ExerciseController(ExerciseRepository exerciseRepository, ModuleRepository moduleRepository) {
        this.exerciseRepository = exerciseRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<ExerciseResponse> list(@PathVariable Long moduleId) {
        Module module = findModule(moduleId);
        return exerciseRepository.findByModule(module)
                .stream()
                .map(ExerciseResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseResponse create(@PathVariable Long moduleId, @Valid @RequestBody ExerciseRequest request) {
        Module module = findModule(moduleId);

        Exercise exercise = new Exercise();
        request.applyTo(exercise, module);
        return ExerciseResponse.from(exerciseRepository.save(exercise));
    }

    @PutMapping("/{exerciseId}")
    public ExerciseResponse update(@PathVariable Long moduleId,
                                   @PathVariable Long exerciseId,
                                   @Valid @RequestBody ExerciseRequest request) {
        Module module = findModule(moduleId);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercício não encontrado para id=" + exerciseId));

        if (exercise.getModule() == null || !exercise.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Exercício id=" + exerciseId + " não pertence ao módulo id=" + moduleId);
        }

        request.applyTo(exercise, module);

        return ExerciseResponse.from(exerciseRepository.save(exercise));
    }

    @DeleteMapping("/{exerciseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long moduleId,
                       @PathVariable Long exerciseId) {
        findModule(moduleId);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercício não encontrado para id=" + exerciseId));

        if (exercise.getModule() == null || !exercise.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Exercício id=" + exerciseId + " não pertence ao módulo id=" + moduleId);
        }

        exerciseRepository.delete(exercise);
    }

    private Module findModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
    }
}
