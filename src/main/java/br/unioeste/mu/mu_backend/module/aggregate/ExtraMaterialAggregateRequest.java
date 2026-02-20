package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import jakarta.validation.constraints.NotBlank;

public class ExtraMaterialAggregateRequest {

    @NotBlank(message = "Tipo é obrigatório")
    private String type;

    @NotBlank(message = "URL é obrigatória")
    private String url;

    public ExtraMaterialAggregateRequest() {
    }

    public ExtraMaterial toExtraMaterial(Lesson lesson) {
        ExtraMaterial material = new ExtraMaterial();
        material.setType(type);
        material.setUrl(url);
        material.setLesson(lesson);
        return material;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
