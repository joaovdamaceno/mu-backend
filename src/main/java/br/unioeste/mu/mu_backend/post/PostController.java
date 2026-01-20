package br.unioeste.mu.mu_backend.post;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class PostController {

    private final PostRepository postRepository;
    private final PostSectionRepository sectionRepository;

    public PostController(PostRepository postRepository, PostSectionRepository sectionRepository) {
        this.postRepository = postRepository;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping
    public List<Post> list() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        if (post.getSections() != null) {
            for (PostSection section : post.getSections()) {
                section.setPost(post);
            }
        }
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
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

                    return ResponseEntity.ok(postRepository.save(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
