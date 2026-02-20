package br.unioeste.mu.mu_backend.module.aggregate;

public class ExtraMaterialAggregateResponse {

    private final Long id;
    private final String type;
    private final String url;
    private final Long lessonId;

    public ExtraMaterialAggregateResponse(Long id, String type, String url, Long lessonId) {
        this.id = id;
        this.type = type;
        this.url = url;
        this.lessonId = lessonId;
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

    public Long getLessonId() {
        return lessonId;
    }
}
