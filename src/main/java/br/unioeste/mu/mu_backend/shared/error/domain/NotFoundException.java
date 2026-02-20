package br.unioeste.mu.mu_backend.shared.error.domain;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
