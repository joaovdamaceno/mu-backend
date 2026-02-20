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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
}
