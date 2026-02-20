package br.unioeste.mu.mu_backend.auth;

import br.unioeste.mu.mu_backend.user.User;
import br.unioeste.mu.mu_backend.user.UserRepository;
import br.unioeste.mu.mu_backend.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "local"})
public class DefaultUsersBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultUsersBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapAdminPassword;

    public DefaultUsersBootstrapRunner(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder,
                                       @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String bootstrapAdminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminPassword = bootstrapAdminPassword;
    }

    @Override
    public void run(String... args) {
        if (bootstrapAdminPassword == null || bootstrapAdminPassword.isBlank()) {
            log.info("BOOTSTRAP_ADMIN_PASSWORD not defined; skipping default admin bootstrap.");
            return;
        }

        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Default admin user already exists; skipping bootstrap.");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode(bootstrapAdminPassword));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        log.info("Default admin user created for local/dev profile bootstrap.");
    }
}
