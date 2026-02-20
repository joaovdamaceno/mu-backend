package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LessonRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Slug é obrigatório")
    private String slug;

    @NotBlank(message = "Resumo é obrigatório")
    private String summary;

    @NotBlank(message = "URL do vídeo é obrigatória")
    private String videoUrl;

    @NotNull(message = "Ordem é obrigatória")
    private Integer orderIndex;

    public LessonRequest() {
    }

    public Lesson toLesson(Module module) {
        Lesson lesson = new Lesson();
        lesson.setTitle(this.title);
        lesson.setSlug(this.slug);
        lesson.setSummary(this.summary);
        lesson.setVideoUrl(this.videoUrl);
        lesson.setOrderIndex(this.orderIndex);
        lesson.setModule(module);
        return lesson;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
