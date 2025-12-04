package br.unioeste.mu.mu_backend.registration;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByEmail(String email);
}
