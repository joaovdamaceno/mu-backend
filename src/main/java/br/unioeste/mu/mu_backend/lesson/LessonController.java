package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import br.unioeste.mu.mu_backend.shared.api.DeleteResponse;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        Module module = findModule(moduleId);
        return lessonRepository.findByModuleOrderByOrderIndexAsc(module)
                .stream()
                .map(LessonResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonResponse create(@PathVariable Long moduleId, @Valid @RequestBody LessonRequest request) {
        Module module = findModule(moduleId);
        Lesson lesson = new Lesson();
        request.applyTo(lesson, module);
        return LessonResponse.from(lessonRepository.save(lesson));
    }

    @PutMapping("/{lessonId}")
    public LessonResponse update(@PathVariable Long moduleId,
                                 @PathVariable Long lessonId,
                                 @Valid @RequestBody LessonRequest request) {
        Module module = findModule(moduleId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));

        if (lesson.getModule() == null || !lesson.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Lição id=" + lessonId + " não pertence ao módulo id=" + moduleId);
        }

        request.applyTo(lesson, module);

        return LessonResponse.from(lessonRepository.save(lesson));
    }
    @DeleteMapping("/{lessonId}")
    public DeleteResponse delete(@PathVariable Long moduleId,
                                 @PathVariable Long lessonId) {
        findModule(moduleId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));

        if (lesson.getModule() == null || !lesson.getModule().getId().equals(moduleId)) {
            throw new BusinessValidationException("Lição id=" + lessonId + " não pertence ao módulo id=" + moduleId);
        }

        lessonRepository.delete(lesson);
        return DeleteResponse.of("Lição removida com sucesso.");
    }

    private Module findModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Módulo não encontrado para id=" + moduleId));
    }

}
