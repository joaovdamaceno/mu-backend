package br.unioeste.mu.mu_backend.shared.error.domain;

public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }
}
