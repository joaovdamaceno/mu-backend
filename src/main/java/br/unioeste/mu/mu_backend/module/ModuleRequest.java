package br.unioeste.mu.mu_backend.module;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModuleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String notes;

    private boolean published = true;

    private final Map<String, Object> unknownFields = new LinkedHashMap<>();

    public ModuleRequest() {
    }

    @JsonAnySetter
    public void collectUnknownField(String field, Object value) {
        unknownFields.put(field, value);
    }

    @AssertTrue(message = "Payload contém campos não permitidos")
    public boolean isPayloadValid() {
        return unknownFields.isEmpty();
    }

    public Module toModule() {
        Module module = new Module();
        applyTo(module);
        return module;
    }

    public void applyTo(Module module) {
        module.setTitle(title);
        module.setNotes(notes);
        module.setPublished(published);
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
}
