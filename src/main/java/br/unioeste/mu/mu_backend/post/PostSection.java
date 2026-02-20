package br.unioeste.mu.mu_backend.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "post_sections")
public class PostSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Min(value = 0, message = "Posição deve ser maior ou igual a zero")
    private int position;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    public PostSection() {
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @AssertTrue(message = "Seção deve conter texto ou imagem")
    public boolean isContentValid() {
        return hasText(text) || hasText(imageUrl);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
