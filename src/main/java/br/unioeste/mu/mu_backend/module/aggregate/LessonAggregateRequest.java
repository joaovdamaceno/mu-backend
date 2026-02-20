package br.unioeste.mu.mu_backend.module.aggregate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LessonAggregateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Summary is required")
    private String summary;

    @NotBlank(message = "Video URL is required")
    private String videoUrl;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    @Valid
    private List<ExerciseAggregateRequest> exercises = new ArrayList<>();

    @Valid
    private List<ExtraMaterialAggregateRequest> extraMaterials = new ArrayList<>();

    public LessonAggregateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<ExerciseAggregateRequest> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseAggregateRequest> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }

    public List<ExtraMaterialAggregateRequest> getExtraMaterials() {
        return extraMaterials;
    }

    public void setExtraMaterials(List<ExtraMaterialAggregateRequest> extraMaterials) {
        this.extraMaterials = extraMaterials != null ? extraMaterials : new ArrayList<>();
    }
}
