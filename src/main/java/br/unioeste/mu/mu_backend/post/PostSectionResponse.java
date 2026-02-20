package br.unioeste.mu.mu_backend.post;

public class PostSectionResponse {

    private final Long id;
    private final String imageUrl;
    private final String text;
    private final int position;

    public PostSectionResponse(PostSection section) {
        this.id = section.getId();
        this.imageUrl = section.getImageUrl();
        this.text = section.getText();
        this.position = section.getPosition();
    }

    public static PostSectionResponse from(PostSection section) {
        return new PostSectionResponse(section);
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public int getPosition() {
        return position;
    }
}
