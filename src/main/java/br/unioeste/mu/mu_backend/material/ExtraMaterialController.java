package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons/{lessonId}/materials")
public class ExtraMaterialController {

    private final ExtraMaterialRepository extraMaterialRepository;
    private final LessonRepository lessonRepository;

    public ExtraMaterialController(ExtraMaterialRepository extraMaterialRepository, LessonRepository lessonRepository) {
        this.extraMaterialRepository = extraMaterialRepository;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping
    public List<ExtraMaterial> list(@PathVariable Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));
        return extraMaterialRepository.findByLesson(lesson);
    }

    @PostMapping
    public ExtraMaterial create(@PathVariable Long lessonId, @Valid @RequestBody ExtraMaterialRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));
        ExtraMaterial material = request.toExtraMaterial(lesson);
        return extraMaterialRepository.save(material);
    }
}
