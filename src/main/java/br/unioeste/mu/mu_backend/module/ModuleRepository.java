package br.unioeste.mu.mu_backend.module;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Page<Module> findAll(Pageable pageable);
}
