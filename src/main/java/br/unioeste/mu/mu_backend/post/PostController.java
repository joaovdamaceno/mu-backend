package br.unioeste.mu.mu_backend.post;

import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;
    private final PostSectionRepository sectionRepository;

    public PostController(PostRepository postRepository, PostSectionRepository sectionRepository) {
        this.postRepository = postRepository;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping
    public Page<Post> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                           @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "id"));
        return postRepository.findAll(pageRequest);
    }

    @GetMapping("/{id}")
    public Post get(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));
    }

    @PostMapping
    public Post create(@Valid @RequestBody Post post) {
        if (post.getSections() != null) {
            for (PostSection section : post.getSections()) {
                section.setPost(post);
            }
        }
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody Post updatedPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));

        post.setTitle(updatedPost.getTitle());
        post.setSlug(updatedPost.getSlug());
        post.setSummary(updatedPost.getSummary());
        post.setCoverImageUrl(updatedPost.getCoverImageUrl());
        post.setAuthorName(updatedPost.getAuthorName());
        post.setStatus(updatedPost.getStatus());

        post.getSections().clear();
        if (updatedPost.getSections() != null) {
            for (PostSection section : updatedPost.getSections()) {
                section.setPost(post);
                post.getSections().add(section);
            }
        }

        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));

        postRepository.delete(post);
    }
}
