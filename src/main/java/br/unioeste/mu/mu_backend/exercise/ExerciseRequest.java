package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.shared.validation.HttpOrHttpsUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Nome do juiz online é obrigatório")
    private String ojName;

    @NotBlank(message = "URL do juiz online é obrigatória")
    @HttpOrHttpsUrl(message = "URL do juiz online deve ser válida e usar http:// ou https://")
    private String ojUrl;

    @NotNull(message = "Dificuldade é obrigatória")
    private ExerciseDifficulty difficulty;

    private List<String> tags = new ArrayList<>();

    public ExerciseRequest() {
    }

    public Exercise toExercise(Module module, Lesson lesson) {
        Exercise exercise = new Exercise();
        exercise.setTitle(normalizeRequired(title));
        exercise.setOjName(normalizeRequired(ojName));
        exercise.setOjUrl(normalizeRequired(ojUrl));
        exercise.setDifficulty(this.difficulty);
        exercise.setTags(normalizeTags(tags));
        exercise.setModule(module);
        exercise.setLesson(lesson);
        return exercise;
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private List<String> normalizeTags(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }

        return values.stream()
                .filter(tag -> tag != null)
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOjName() {
        return ojName;
    }

    public void setOjName(String ojName) {
        this.ojName = ojName;
    }

    public String getOjUrl() {
        return ojUrl;
    }

    public void setOjUrl(String ojUrl) {
        this.ojUrl = ojUrl;
    }

    public ExerciseDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(ExerciseDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }
}
