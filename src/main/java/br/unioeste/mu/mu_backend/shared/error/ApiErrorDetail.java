package br.unioeste.mu.mu_backend.shared.error;

public record ApiErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {
}
