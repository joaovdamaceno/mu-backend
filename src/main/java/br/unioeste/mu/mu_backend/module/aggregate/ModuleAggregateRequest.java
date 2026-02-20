package br.unioeste.mu.mu_backend.module.aggregate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class ModuleAggregateRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String notes;

    private boolean published = true;

    @Valid
    private List<LessonAggregateRequest> lessons = new ArrayList<>();

    public ModuleAggregateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<LessonAggregateRequest> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonAggregateRequest> lessons) {
        this.lessons = lessons != null ? lessons : new ArrayList<>();
    }
}
