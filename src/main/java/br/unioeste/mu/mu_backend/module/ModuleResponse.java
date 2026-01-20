package br.unioeste.mu.mu_backend.module;

import java.time.LocalDateTime;

public class ModuleResponse {

    private final Long id;
    private final String title;
    private final String notes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean published;

    public ModuleResponse(Module module) {
        this.id = module.getId();
        this.title = module.getTitle();
        this.notes = module.getNotes();
        this.createdAt = module.getCreatedAt();
        this.updatedAt = module.getUpdatedAt();
        this.published = module.isPublished();
    }

    public static ModuleResponse from(Module module) {
        return new ModuleResponse(module);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isPublished() {
        return published;
    }
}
