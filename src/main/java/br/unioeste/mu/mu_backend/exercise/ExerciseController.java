package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/exercises")
@CrossOrigin
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;

    public ExerciseController(ExerciseRepository exerciseRepository, ModuleRepository moduleRepository) {
        this.exerciseRepository = exerciseRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<Exercise> list(@PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return exerciseRepository.findByModule(module);
    }

    @PostMapping
    public Exercise create(@PathVariable Long moduleId, @RequestBody Exercise exercise) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        exercise.setModule(module);
        return exerciseRepository.save(exercise);
    }
}
