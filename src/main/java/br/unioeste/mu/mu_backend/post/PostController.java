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

import java.util.function.Function;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostRepository postRepository;
    private static final Function<Post, PostResponse> TO_RESPONSE = PostResponse::from;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public Page<PostResponse> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                   @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt", "id"));
        return postRepository.findAll(pageRequest).map(TO_RESPONSE);
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(TO_RESPONSE)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));
    }

    @PostMapping
    public PostResponse create(@Valid @RequestBody PostRequest request) {
        return TO_RESPONSE.apply(postRepository.save(request.toPost()));
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));

        request.applyTo(post);

        return TO_RESPONSE.apply(postRepository.save(post));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post não encontrado para id=" + id));

        postRepository.delete(post);
    }
}
