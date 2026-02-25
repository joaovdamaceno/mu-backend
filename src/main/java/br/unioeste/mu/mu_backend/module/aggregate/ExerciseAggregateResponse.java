package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;

import java.util.List;

public class ExerciseAggregateResponse {

    private final Long id;
    private final String title;
    private final String ojUrl;
    private final ExerciseDifficulty difficulty;
    private final List<String> tags;
    private final Long moduleId;

    public ExerciseAggregateResponse(
            Long id,
            String title,
            String ojUrl,
            ExerciseDifficulty difficulty,
            List<String> tags,
            Long moduleId
    ) {
        this.id = id;
        this.title = title;
        this.ojUrl = ojUrl;
        this.difficulty = difficulty;
        this.tags = tags;
        this.moduleId = moduleId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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
}
