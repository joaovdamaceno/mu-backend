package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.GET, RequestMethod.POST}
)
public class LessonController {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonController(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<LessonResponse> list(@PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return lessonRepository.findByModuleOrderByOrderIndexAsc(module)
                .stream()
                .map(LessonResponse::from)
                .toList();
    }

    @PostMapping
    public LessonResponse create(@PathVariable Long moduleId, @Valid @RequestBody LessonRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Lesson lesson = request.toLesson(module);
        return LessonResponse.from(lessonRepository.save(lesson));
    }
}
