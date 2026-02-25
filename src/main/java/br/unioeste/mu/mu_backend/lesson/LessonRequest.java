package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.shared.validation.HttpOrHttpsUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LessonRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "URL do vídeo é obrigatória")
    @HttpOrHttpsUrl(message = "URL do vídeo deve ser válida e usar http:// ou https://")
    private String videoUrl;

    @NotNull(message = "Ordem é obrigatória")
    private Integer orderIndex;

    public LessonRequest() {
    }

    public Lesson toLesson(Module module) {
        Lesson lesson = new Lesson();
        applyTo(lesson, module);
        return lesson;
    }

    public void applyTo(Lesson lesson, Module module) {
        lesson.setTitle(normalizeRequired(title));
        lesson.setVideoUrl(normalizeRequired(videoUrl));
        lesson.setOrderIndex(this.orderIndex);
        lesson.setModule(module);
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
