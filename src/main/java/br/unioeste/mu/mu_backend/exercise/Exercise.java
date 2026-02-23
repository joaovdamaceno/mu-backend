package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Online judge name is required")
    @Column(name = "oj_name")
    private String ojName;

    @NotBlank(message = "Online judge URL is required")
    @Column(name = "oj_url")
    private String ojUrl;

    @NotNull(message = "Difficulty is required")
    @Enumerated(EnumType.STRING)
    private ExerciseDifficulty difficulty;

    @ElementCollection
    @CollectionTable(name = "exercise_tags", joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "module_id")
    @NotNull(message = "Module is required")
    @JsonIgnore
    private Module module;

    public Exercise() {
    }

    public Long getId() {
        return id;
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

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
