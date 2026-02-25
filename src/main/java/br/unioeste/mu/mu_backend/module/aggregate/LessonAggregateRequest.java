package br.unioeste.mu.mu_backend.module.aggregate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LessonAggregateRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "URL do vídeo é obrigatória")
    private String videoUrl;

    @NotNull(message = "Ordem é obrigatória")
    private Integer orderIndex;

    public LessonAggregateRequest() {
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
