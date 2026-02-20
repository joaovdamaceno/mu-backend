package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import jakarta.validation.constraints.NotBlank;

public class ExtraMaterialRequest {

    @NotBlank(message = "Tipo é obrigatório")
    private String type;

    @NotBlank(message = "URL é obrigatória")
    private String url;

    public ExtraMaterialRequest() {
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

    public ExtraMaterial toExtraMaterial(Lesson lesson) {
        ExtraMaterial material = new ExtraMaterial();
        material.setType(type);
        material.setUrl(url);
        material.setLesson(lesson);
        return material;
    }
}
