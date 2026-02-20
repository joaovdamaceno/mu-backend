package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons")
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
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
        return lessonRepository.findByModuleOrderByOrderIndexAsc(module)
                .stream()
                .map(LessonResponse::from)
                .toList();
    }

    @PostMapping
    public LessonResponse create(@PathVariable Long moduleId, @Valid @RequestBody LessonRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
        Lesson lesson = request.toLesson(module);
        return LessonResponse.from(lessonRepository.save(lesson));
    }
}
