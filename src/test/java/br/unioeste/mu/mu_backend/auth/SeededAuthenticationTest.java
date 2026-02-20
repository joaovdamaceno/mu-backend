package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.user.User;
import br.unioeste.mu.mu_backend.user.UserRepository;
import br.unioeste.mu.mu_backend.user.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeededAuthenticationTest {

    @Test
    void shouldCreateAdminUsingRuntimePasswordAndBcryptEncoderContract() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("super-secret")).thenReturn("encoded-hash");

        DefaultUsersBootstrapRunner runner = new DefaultUsersBootstrapRunner(userRepository, passwordEncoder, "super-secret");
        runner.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User created = userCaptor.getValue();
        assertEquals("admin", created.getUsername());
        assertEquals("encoded-hash", created.getPasswordHash());
        assertEquals(UserRole.ADMIN, created.getRole());
    }

    @Test
    void shouldNotRegisterBootstrapRunnerWhenProdProfileIsActive() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withPropertyValues("spring.profiles.active=prod")
                .withUserConfiguration(DefaultUsersBootstrapRunner.class, SupportBeans.class);

        contextRunner.run(context -> assertFalse(context.containsBean("defaultUsersBootstrapRunner")));
    }

    @Configuration
    static class SupportBeans {
        @Bean
        UserRepository userRepository() {
            UserRepository repository = mock(UserRepository.class);
            when(repository.findByUsername("admin")).thenReturn(Optional.empty());
            return repository;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
    }
}
