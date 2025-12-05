package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.module.Module;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Online judge name is required")
    private String ojName;

    @NotBlank(message = "Online judge URL is required")
    private String ojUrl;

    @NotNull(message = "Difficulty is required")
    private ExerciseDifficulty difficulty;

    private List<String> tags = new ArrayList<>();

    public ExerciseRequest() {
    }

    public Exercise toExercise(Module module, Lesson lesson) {
        Exercise exercise = new Exercise();
        exercise.setTitle(this.title);
        exercise.setOjName(this.ojName);
        exercise.setOjUrl(this.ojUrl);
        exercise.setDifficulty(this.difficulty);
        exercise.setTags(this.tags);
        exercise.setModule(module);
        exercise.setLesson(lesson);
        return exercise;
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
