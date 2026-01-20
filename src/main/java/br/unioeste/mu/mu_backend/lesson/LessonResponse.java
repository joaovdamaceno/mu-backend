package br.unioeste.mu.mu_backend.lesson;

public class LessonResponse {

    private final Long id;
    private final String title;
    private final String slug;
    private final String summary;
    private final String videoUrl;
    private final Integer orderIndex;
    private final Long moduleId;

    public LessonResponse(Lesson lesson) {
        this.id = lesson.getId();
        this.title = lesson.getTitle();
        this.slug = lesson.getSlug();
        this.summary = lesson.getSummary();
        this.videoUrl = lesson.getVideoUrl();
        this.orderIndex = lesson.getOrderIndex();
        this.moduleId = lesson.getModule() != null ? lesson.getModule().getId() : null;
    }

    public static LessonResponse from(Lesson lesson) {
        return new LessonResponse(lesson);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getSummary() {
        return summary;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Long getModuleId() {
        return moduleId;
    }
}
