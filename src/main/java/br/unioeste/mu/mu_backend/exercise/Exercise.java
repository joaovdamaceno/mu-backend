package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String link;

    private short difficulty;

    @ManyToOne
    @JoinColumn(name = "module_id")
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public short getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(short difficulty) {
        this.difficulty = difficulty;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
