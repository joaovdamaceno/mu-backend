package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.shared.validation.HttpOrHttpsUrl;
import jakarta.validation.constraints.NotBlank;

public class ExtraMaterialRequest {

    @NotBlank(message = "Tipo é obrigatório")
    private String type;

    @NotBlank(message = "URL é obrigatória")
    @HttpOrHttpsUrl(message = "URL deve ser válida e usar http:// ou https://")
    private String url;

    public ExtraMaterialRequest() {
    }

    public ExtraMaterial toExtraMaterial(Module module) {
        ExtraMaterial material = new ExtraMaterial();
        applyTo(material, module);
        return material;
    }

    public void applyTo(ExtraMaterial material, Module module) {
        material.setType(normalizeRequired(type));
        material.setUrl(normalizeRequired(url));
        material.setModule(module);
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
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
