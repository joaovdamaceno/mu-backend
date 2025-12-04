package br.unioeste.mu.mu_backend.registration;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin
public class RegistrationController {

    private final RegistrationRepository repository;

    public RegistrationController(RegistrationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Registration create(@RequestBody Registration request) {
        return repository.save(request);
    }

    @GetMapping
    public List<Registration> list() {
        return repository.findAll();
    }
}
