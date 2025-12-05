package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.module.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByModule(Module module);

    List<Exercise> findByLesson(Lesson lesson);
}
