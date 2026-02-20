package br.unioeste.mu.mu_backend.post;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PostResponse {

    private final Long id;
    private final String title;
    private final String tag;
    private final String slug;
    private final String summary;
    private final String coverImageUrl;
    private final String authorName;
    private final String status;
    private final String mainText;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<PostSectionResponse> sections;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.tag = post.getTag();
        this.slug = post.getSlug();
        this.summary = post.getSummary();
        this.coverImageUrl = post.getCoverImageUrl();
        this.authorName = post.getAuthorName();
        this.status = post.getStatus();
        this.mainText = post.getMainText();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.sections = post.getSections() == null ? Collections.emptyList() : post.getSections().stream()
                .sorted(Comparator.comparingInt(PostSection::getPosition))
                .map(PostSectionResponse::from)
                .toList();
    }

    public static PostResponse from(Post post) {
        return new PostResponse(post);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public String getSlug() {
        return slug;
    }

    public String getSummary() {
        return summary;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getStatus() {
        return status;
    }

    public String getMainText() {
        return mainText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<PostSectionResponse> getSections() {
        return sections;
    }
}
