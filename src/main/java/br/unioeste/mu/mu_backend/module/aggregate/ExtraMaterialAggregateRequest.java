package br.unioeste.mu.mu_backend.module.aggregate;

import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.module.Module;
import jakarta.validation.constraints.NotBlank;

public class ExtraMaterialAggregateRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "URL é obrigatória")
    private String url;

    public ExtraMaterialAggregateRequest() {
    }

    public ExtraMaterial toExtraMaterial(Module module) {
        ExtraMaterial material = new ExtraMaterial();
        material.setTitle(title);
        material.setUrl(url);
        material.setModule(module);
        return material;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
