package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtraMaterialRepository extends JpaRepository<ExtraMaterial, Long> {

    List<ExtraMaterial> findByLesson(Lesson lesson);
}
