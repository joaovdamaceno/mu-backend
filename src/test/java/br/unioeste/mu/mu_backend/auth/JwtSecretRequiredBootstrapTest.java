package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.MuBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtSecretRequiredBootstrapTest {

    @Test
    void shouldFailStartupWithoutJwtSecretWhenProdProfileIsActive() {
        Exception exception = assertThrows(Exception.class, () -> {
            try (ConfigurableApplicationContext ignored = new SpringApplicationBuilder(MuBackendApplication.class)
                    .web(WebApplicationType.NONE)
                    .profiles("prod")
                    .properties(
                            "spring.datasource.url=jdbc:h2:mem:jwt-secret-required-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                            "spring.datasource.driverClassName=org.h2.Driver",
                            "spring.datasource.username=sa",
                            "spring.datasource.password=",
                            "spring.jpa.hibernate.ddl-auto=create-drop",
                            "spring.flyway.enabled=false"
                    )
                    .run()) {
                // expected startup failure
            }
        });

        Throwable root = rootCause(exception);
        assertTrue(root instanceof IllegalArgumentException || root instanceof BeanCreationException);
        assertTrue(root.getMessage().contains("jwt.secret") || root.getMessage().contains("JWT_SECRET"));
    }

    private Throwable rootCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }
}
