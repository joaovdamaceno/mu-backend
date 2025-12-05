package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons/{lessonId}/exercises")
@CrossOrigin
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
    public List<Exercise> list(@PathVariable Long moduleId, @PathVariable Long lessonId) {
        findModule(moduleId);
        Lesson lesson = findLesson(lessonId);
        validateLessonBelongsToModule(moduleId, lesson);
        return exerciseRepository.findByLesson(lesson);
    }

    @PostMapping
    public Exercise create(@PathVariable Long moduleId, @PathVariable Long lessonId, @Valid @RequestBody ExerciseRequest request) {
        Module module = findModule(moduleId);
        Lesson lesson = findLesson(lessonId);
        validateLessonBelongsToModule(moduleId, lesson);

        Exercise exercise = request.toExercise(module, lesson);
        return exerciseRepository.save(exercise);
    }

    private Module findModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
    }

    private Lesson findLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
    }

    private void validateLessonBelongsToModule(Long moduleId, Lesson lesson) {
        if (lesson.getModule() == null || !lesson.getModule().getId().equals(moduleId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson does not belong to the specified module");
        }
    }
}
