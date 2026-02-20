package br.unioeste.mu.mu_backend.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "username é obrigatório")
    public String username;

    @NotBlank(message = "password é obrigatório")
    public String password;
}
