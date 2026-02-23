package br.unioeste.mu.mu_backend.module.aggregate;

public class ExtraMaterialAggregateResponse {

    private final Long id;
    private final String type;
    private final String url;
    private final Long moduleId;

    public ExtraMaterialAggregateResponse(Long id, String type, String url, Long moduleId) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.moduleId = moduleId;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public Long getModuleId() {
        return moduleId;
    }
}
