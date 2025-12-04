package br.unioeste.mu.mu_backend.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSectionRepository extends JpaRepository<PostSection, Long> {

    List<PostSection> findByPostOrderByPositionAsc(Post post);
}
