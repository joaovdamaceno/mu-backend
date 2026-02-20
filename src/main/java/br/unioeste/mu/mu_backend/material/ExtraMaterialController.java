package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
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

    @PutMapping("/{materialId}")
    public ExtraMaterial update(@PathVariable Long lessonId,
                                @PathVariable Long materialId,
                                @Valid @RequestBody ExtraMaterialRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada para id=" + lessonId));

        ExtraMaterial material = extraMaterialRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material extra não encontrado para id=" + materialId));

        if (material.getLesson() == null || !material.getLesson().getId().equals(lessonId)) {
            throw new BusinessValidationException("Material extra id=" + materialId + " não pertence à lição id=" + lessonId);
        }

        material.setType(request.getType());
        material.setUrl(request.getUrl());
        material.setLesson(lesson);

        return extraMaterialRepository.save(material);
    }
}
