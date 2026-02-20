package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons/{lessonId}/exercises")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.GET, RequestMethod.POST}
)
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public ExerciseController(ExerciseRepository exerciseRepository, ModuleRepository moduleRepository, LessonRepository lessonRepository) {
        this.exerciseRepository = exerciseRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    public List<ExerciseResponse> list(@PathVariable Long moduleId, @PathVariable Long lessonId) {
        findModule(moduleId);
        Lesson lesson = findLesson(lessonId);
        validateLessonBelongsToModule(moduleId, lesson);
        return exerciseRepository.findByLesson(lesson)
                .stream()
                .map(ExerciseResponse::from)
                .toList();
    }

    @PostMapping
    public ExerciseResponse create(@PathVariable Long moduleId, @PathVariable Long lessonId, @Valid @RequestBody ExerciseRequest request) {
        Module module = findModule(moduleId);
        Lesson lesson = findLesson(lessonId);
        validateLessonBelongsToModule(moduleId, lesson);

        Exercise exercise = request.toExercise(module, lesson);
        return ExerciseResponse.from(exerciseRepository.save(exercise));
    }

    private Module findModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
    }

    private Lesson findLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));
    }

    private void validateLessonBelongsToModule(Long moduleId, Lesson lesson) {
        if (lesson.getModule() == null || !lesson.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Lição id=" + lesson.getId() + " não pertence ao módulo id=" + moduleId);
        }
    }
}
