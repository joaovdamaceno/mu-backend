package br.unioeste.mu.mu_backend.post;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String title;

    @Size(max = 50, message = "Tag deve ter no máximo 50 caracteres")
    private String tag;

    @NotBlank(message = "Slug é obrigatório")
    @Size(max = 200, message = "Slug deve ter no máximo 200 caracteres")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug deve conter apenas letras minúsculas, números e hífens")
    private String slug;

    private String summary;

    private String coverImageUrl;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 200, message = "Autor deve ter no máximo 200 caracteres")
    private String authorName;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 50, message = "Status deve ter no máximo 50 caracteres")
    private String status;

    private String mainText;

    @Valid
    private List<PostSectionRequest> sections = new ArrayList<>();

    private final Map<String, Object> unknownFields = new LinkedHashMap<>();

    public PostRequest() {
    }

    @JsonAnySetter
    public void collectUnknownField(String field, Object value) {
        unknownFields.put(field, value);
    }

    @AssertTrue(message = "Payload contém campos não permitidos")
    public boolean isPayloadValid() {
        return unknownFields.isEmpty();
    }

    public Post toPost() {
        Post post = new Post();
        applyTo(post);
        return post;
    }

    public void applyTo(Post post) {
        post.setTitle(title);
        post.setTag(tag);
        post.setSlug(slug);
        post.setSummary(summary);
        post.setCoverImageUrl(coverImageUrl);
        post.setAuthorName(authorName);
        post.setStatus(status);
        post.setMainText(mainText);

        post.getSections().clear();
        for (PostSectionRequest sectionRequest : sections) {
            PostSection section = sectionRequest.toEntity();
            section.setPost(post);
            post.getSections().add(section);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public List<PostSectionRequest> getSections() {
        return sections;
    }

    public void setSections(List<PostSectionRequest> sections) {
        this.sections = sections != null ? sections : new ArrayList<>();
    }
}
