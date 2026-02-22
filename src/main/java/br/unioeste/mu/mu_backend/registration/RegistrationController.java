package br.unioeste.mu.mu_backend.registration;

import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@Validated
public class RegistrationController {

    private static final int MAX_PAGE_SIZE = 100;

    private final RegistrationRepository registrationRepository;

    public RegistrationController(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @GetMapping
    public Page<RegistrationResponse> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(MAX_PAGE_SIZE) int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return registrationRepository.findAll(pageRequest).map(RegistrationResponse::from);
    }

    @GetMapping("/{id}")
    public RegistrationResponse get(@PathVariable Long id) {
        return registrationRepository.findById(id)
                .map(RegistrationResponse::from)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada para id=" + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse create(@Valid @RequestBody RegistrationRequest request) {
        return RegistrationResponse.from(registrationRepository.save(request.toRegistration()));
    }

    @PutMapping("/{id}")
    public RegistrationResponse update(@PathVariable Long id, @Valid @RequestBody RegistrationRequest request) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada para id=" + id));

        request.applyTo(registration);
        return RegistrationResponse.from(registrationRepository.save(registration));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inscrição não encontrada para id=" + id));

        registrationRepository.delete(registration);
    }
}
