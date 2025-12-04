package br.unioeste.mu.mu_backend.post;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
