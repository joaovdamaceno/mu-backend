package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.profiles.active=prod",
        "spring.datasource.url=jdbc:h2:mem:prod-bootstrap-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class ProdProfileDefaultUserBootstrapIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldNotCreateDefaultUsersImplicitlyInProdProfile() {
        assertEquals(0, userRepository.count());
        assertTrue(userRepository.findByUsername("admin").isEmpty());
    }
}
