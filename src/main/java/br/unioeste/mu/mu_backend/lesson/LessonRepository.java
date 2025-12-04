package br.unioeste.mu.mu_backend.lesson;

import br.unioeste.mu.mu_backend.module.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleOrderByPositionAsc(Module module);
}
