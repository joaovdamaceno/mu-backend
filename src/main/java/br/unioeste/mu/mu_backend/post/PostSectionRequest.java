package br.unioeste.mu.mu_backend.post;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;

import java.util.LinkedHashMap;
import java.util.Map;

public class PostSectionRequest {

    private String imageUrl;

    private String text;

    @Min(value = 0, message = "Posição deve ser maior ou igual a zero")
    private int position;

    private final Map<String, Object> unknownFields = new LinkedHashMap<>();

    public PostSectionRequest() {
    }

    @JsonAnySetter
    public void collectUnknownField(String field, Object value) {
        unknownFields.put(field, value);
    }

    @AssertTrue(message = "Payload contém campos não permitidos")
    public boolean isPayloadValid() {
        return unknownFields.isEmpty();
    }

    @AssertTrue(message = "Seção deve conter texto ou imagem")
    public boolean isContentValid() {
        return hasText(text) || hasText(imageUrl);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public PostSection toEntity() {
        PostSection section = new PostSection();
        section.setImageUrl(imageUrl);
        section.setText(text);
        section.setPosition(position);
        return section;
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
}
