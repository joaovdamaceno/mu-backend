package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons")
@CrossOrigin
public class LessonController {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonController(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @GetMapping
    public List<Lesson> list(@PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId).orElseThrow();
        return lessonRepository.findByModuleOrderByPositionAsc(module);
    }

    @PostMapping
    public Lesson create(@PathVariable Long moduleId, @RequestBody Lesson lesson) {
        Module module = moduleRepository.findById(moduleId).orElseThrow();
        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }
}
