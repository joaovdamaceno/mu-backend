package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;

import java.util.List;

public class ExerciseAggregateResponse {

    private final Long id;
    private final String title;
    private final String ojName;
    private final String ojUrl;
    private final ExerciseDifficulty difficulty;
    private final List<String> tags;
    private final Long moduleId;
    private final Long lessonId;

    public ExerciseAggregateResponse(
            Long id,
            String title,
            String ojName,
            String ojUrl,
            ExerciseDifficulty difficulty,
            List<String> tags,
            Long moduleId,
            Long lessonId
    ) {
        this.id = id;
        this.title = title;
        this.ojName = ojName;
        this.ojUrl = ojUrl;
        this.difficulty = difficulty;
        this.tags = tags;
        this.moduleId = moduleId;
        this.lessonId = lessonId;
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
