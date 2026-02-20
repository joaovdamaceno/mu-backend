package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.module.Module;

import java.util.List;

public class ModuleAggregateResponse {

    private final CreatedModuleResponse module;
    private final List<LessonAggregateResponse> lessons;

    public ModuleAggregateResponse(Module module, List<LessonAggregateResponse> lessons) {
        this.module = new CreatedModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getNotes(),
                module.isPublished()
        );
        this.lessons = lessons;
    }

    public static LessonAggregateResponse lessonFrom(
            Lesson lesson,
            List<Exercise> exercises,
            List<ExtraMaterial> extraMaterials
    ) {
        return new LessonAggregateResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getSlug(),
                lesson.getSummary(),
                lesson.getVideoUrl(),
                lesson.getOrderIndex(),
                exercises.stream().map(exercise -> new ExerciseAggregateResponse(
                        exercise.getId(),
                        exercise.getTitle(),
                        exercise.getOjName(),
                        exercise.getOjUrl(),
                        exercise.getDifficulty(),
                        exercise.getTags(),
                        exercise.getModule() != null ? exercise.getModule().getId() : null,
                        exercise.getLesson() != null ? exercise.getLesson().getId() : null
                )).toList(),
                extraMaterials.stream().map(extraMaterial -> new ExtraMaterialAggregateResponse(
                        extraMaterial.getId(),
                        extraMaterial.getType(),
                        extraMaterial.getUrl(),
                        extraMaterial.getLesson() != null ? extraMaterial.getLesson().getId() : null
                )).toList()
        );
    }

    public CreatedModuleResponse getModule() {
        return module;
    }

    public List<LessonAggregateResponse> getLessons() {
        return lessons;
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
