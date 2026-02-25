package br.unioeste.mu.mu_backend.module.aggregate;

public class LessonAggregateResponse {

    private final Long id;
    private final String title;
    private final String videoUrl;
    private final Integer orderIndex;

    public LessonAggregateResponse(
            Long id,
            String title,
            String videoUrl,
            Integer orderIndex
    ) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public String getVideoUrl() {
        return videoUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }
}
