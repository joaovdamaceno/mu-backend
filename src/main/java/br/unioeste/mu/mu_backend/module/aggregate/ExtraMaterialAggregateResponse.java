package br.unioeste.mu.mu_backend.module.aggregate;

public class ExtraMaterialAggregateResponse {

    private final Long id;
    private final String title;
    private final String url;
    private final Long moduleId;

    public ExtraMaterialAggregateResponse(Long id, String title, String url, Long moduleId) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.moduleId = moduleId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Long getModuleId() {
        return moduleId;
    }
}
