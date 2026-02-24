package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @EntityGraph(attributePaths = "tags")
    List<Exercise> findByModule(Module module);

    List<Exercise> findByModuleIdInOrderByModuleIdAscIdAsc(List<Long> moduleIds);
}
