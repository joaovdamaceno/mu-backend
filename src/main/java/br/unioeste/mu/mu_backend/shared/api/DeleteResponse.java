package br.unioeste.mu.mu_backend.shared.api;

public record DeleteResponse(String message) {

    public static DeleteResponse of(String message) {
        return new DeleteResponse(message);
    }
}
