package br.unioeste.mu.mu_backend.shared.error.domain;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
