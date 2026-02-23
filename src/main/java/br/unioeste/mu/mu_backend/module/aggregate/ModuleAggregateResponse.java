package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ModuleAggregateResponse {

    private final CreatedModuleResponse module;
    private final List<LessonAggregateResponse> lessons;
    private final List<ExerciseAggregateResponse> exercises;
    private final List<ExtraMaterialAggregateResponse> extraMaterials;

    public ModuleAggregateResponse(
            Module module,
            List<LessonAggregateResponse> lessons,
            List<ExerciseAggregateResponse> exercises,
            List<ExtraMaterialAggregateResponse> extraMaterials
    ) {
        this.module = new CreatedModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getNotes(),
                module.isPublished()
        );
        this.lessons = lessons;
        this.exercises = exercises;
        this.extraMaterials = extraMaterials;
    }

    public static LessonAggregateResponse lessonFrom(Lesson lesson) {
        return new LessonAggregateResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getSlug(),
                lesson.getSummary(),
                lesson.getVideoUrl(),
                lesson.getOrderIndex()
        );
    }

    public static ExerciseAggregateResponse exerciseFrom(Exercise exercise) {
        List<String> tags = exercise.getTags() != null
                ? new ArrayList<>(exercise.getTags())
                : List.of();

        return new ExerciseAggregateResponse(
                exercise.getId(),
                exercise.getTitle(),
                exercise.getOjName(),
                exercise.getOjUrl(),
                exercise.getDifficulty(),
                tags,
                exercise.getModule() != null ? exercise.getModule().getId() : null
        );
    }

    public static ExtraMaterialAggregateResponse extraMaterialFrom(ExtraMaterial extraMaterial) {
        return new ExtraMaterialAggregateResponse(
                extraMaterial.getId(),
                extraMaterial.getType(),
                extraMaterial.getUrl(),
                extraMaterial.getModule() != null ? extraMaterial.getModule().getId() : null
        );
    }

    public CreatedModuleResponse getModule() {
        return module;
    }

    public List<LessonAggregateResponse> getLessons() {
        return lessons;
    }

    public List<ExerciseAggregateResponse> getExercises() {
        return exercises;
    }

    public List<ExtraMaterialAggregateResponse> getExtraMaterials() {
        return extraMaterials;
    }

    public static class CreatedModuleResponse {
        private final Long id;
        private final String title;
        private final String notes;
        private final boolean published;

        public CreatedModuleResponse(Long id, String title, String notes, boolean published) {
            this.id = id;
            this.title = title;
            this.notes = notes;
            this.published = published;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getNotes() {
            return notes;
        }

        public boolean isPublished() {
            return published;
        }
    }
}
