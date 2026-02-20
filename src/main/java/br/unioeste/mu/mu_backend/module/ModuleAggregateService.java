package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateResponse;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ModuleAggregateService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExtraMaterialRepository extraMaterialRepository;

    public ModuleAggregateService(
            ModuleRepository moduleRepository,
            LessonRepository lessonRepository,
            ExerciseRepository exerciseRepository,
            ExtraMaterialRepository extraMaterialRepository
    ) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.exerciseRepository = exerciseRepository;
        this.extraMaterialRepository = extraMaterialRepository;
    }

    @Transactional
    public ModuleAggregateResponse createFullModule(ModuleAggregateRequest request) {
        validateAggregatePayload(request);

        Module module = new Module();
        module.setTitle(request.getTitle());
        module.setNotes(request.getNotes());
        module.setPublished(request.isPublished());

        Module persistedModule = moduleRepository.save(module);
        List<LessonAggregateResponse> lessonResponses = new ArrayList<>();

        for (LessonAggregateRequest lessonRequest : request.getLessons()) {
            Lesson lesson = new Lesson();
            lesson.setTitle(lessonRequest.getTitle());
            lesson.setSlug(lessonRequest.getSlug());
            lesson.setSummary(lessonRequest.getSummary());
            lesson.setVideoUrl(lessonRequest.getVideoUrl());
            lesson.setOrderIndex(lessonRequest.getOrderIndex());
            lesson.setModule(persistedModule);

            Lesson persistedLesson = lessonRepository.save(lesson);

            List<Exercise> persistedExercises = lessonRequest.getExercises().stream()
                    .map(exerciseRequest -> exerciseRequest.toExercise(persistedModule, persistedLesson))
                    .map(exerciseRepository::save)
                    .toList();

            List<ExtraMaterial> persistedExtraMaterials = lessonRequest.getExtraMaterials().stream()
                    .map(extraMaterialRequest -> extraMaterialRequest.toExtraMaterial(persistedLesson))
                    .map(extraMaterialRepository::save)
                    .toList();

            lessonResponses.add(ModuleAggregateResponse.lessonFrom(
                    persistedLesson,
                    persistedExercises,
                    persistedExtraMaterials
            ));
        }

        return new ModuleAggregateResponse(persistedModule, lessonResponses);
    }

    private void validateAggregatePayload(ModuleAggregateRequest request) {
        if (request == null) {
            throw invalidPayload("Payload do módulo agregado é obrigatório");
        }

        Set<Integer> usedOrderIndexes = new HashSet<>();
        Set<String> usedSlugs = new HashSet<>();

        for (int lessonIndex = 0; lessonIndex < request.getLessons().size(); lessonIndex++) {
            LessonAggregateRequest lesson = request.getLessons().get(lessonIndex);
            int lessonPosition = lessonIndex + 1;

            if (lesson == null) {
                throw invalidPayload("Lição na posição " + lessonPosition + " está ausente");
            }

            validateRequiredText(lesson.getSlug(), "slug", lessonPosition);

            Integer orderIndex = lesson.getOrderIndex();
            if (orderIndex == null) {
                throw invalidPayload("Lição na posição " + lessonPosition + " deve informar orderIndex");
            }

            if (!usedOrderIndexes.add(orderIndex)) {
                throw invalidPayload("orderIndex duplicado para lições do módulo: " + orderIndex);
            }

            String normalizedSlug = lesson.getSlug().trim().toLowerCase();
            if (!usedSlugs.add(normalizedSlug)) {
                throw invalidPayload("slug duplicado para lições do módulo: " + lesson.getSlug().trim());
            }
        }
    }

    private void validateRequiredText(String value, String fieldName, int lessonPosition) {
        if (value == null || value.trim().isEmpty()) {
            throw invalidPayload("Lição na posição " + lessonPosition + " possui " + fieldName + " inválido");
        }
    }

    private ResponseStatusException invalidPayload(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
