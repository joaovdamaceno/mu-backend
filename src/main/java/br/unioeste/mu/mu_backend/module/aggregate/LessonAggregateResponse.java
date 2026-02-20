package br.unioeste.mu.mu_backend.module.aggregate;

import java.util.List;

public class LessonAggregateResponse {

    private final Long id;
    private final String title;
    private final String slug;
    private final String summary;
    private final String videoUrl;
    private final Integer orderIndex;
    private final List<ExerciseAggregateResponse> exercises;
    private final List<ExtraMaterialAggregateResponse> extraMaterials;

    public LessonAggregateResponse(
            Long id,
            String title,
            String slug,
            String summary,
            String videoUrl,
            Integer orderIndex,
            List<ExerciseAggregateResponse> exercises,
            List<ExtraMaterialAggregateResponse> extraMaterials
    ) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.summary = summary;
        this.videoUrl = videoUrl;
        this.orderIndex = orderIndex;
        this.exercises = exercises;
        this.extraMaterials = extraMaterials;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getSummary() {
        return summary;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public List<ExerciseAggregateResponse> getExercises() {
        return exercises;
    }

    public List<ExtraMaterialAggregateResponse> getExtraMaterials() {
        return extraMaterials;
    }
}
