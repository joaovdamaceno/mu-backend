package br.unioeste.mu.mu_backend.exercise;

import java.util.List;

public class ExerciseResponse {

    private final Long id;
    private final String title;
    private final String ojName;
    private final String ojUrl;
    private final ExerciseDifficulty difficulty;
    private final List<String> tags;
    private final Long moduleId;
    private final Long lessonId;

    public ExerciseResponse(Exercise exercise) {
        this.id = exercise.getId();
        this.title = exercise.getTitle();
        this.ojName = exercise.getOjName();
        this.ojUrl = exercise.getOjUrl();
        this.difficulty = exercise.getDifficulty();
        this.tags = exercise.getTags();
        this.moduleId = exercise.getModule() != null ? exercise.getModule().getId() : null;
        this.lessonId = exercise.getLesson() != null ? exercise.getLesson().getId() : null;
    }

    public static ExerciseResponse from(Exercise exercise) {
        return new ExerciseResponse(exercise);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOjName() {
        return ojName;
    }

    public String getOjUrl() {
        return ojUrl;
    }

    public ExerciseDifficulty getDifficulty() {
        return difficulty;
    }

    public List<String> getTags() {
        return tags;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public Long getLessonId() {
        return lessonId;
    }
}
