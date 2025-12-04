package br.unioeste.mu.mu_backend.material;

import br.unioeste.mu.mu_backend.module.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtraMaterialRepository extends JpaRepository<ExtraMaterial, Long> {

    List<ExtraMaterial> findByModule(Module module);
}
