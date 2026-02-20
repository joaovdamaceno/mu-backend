package br.unioeste.mu.mu_backend.auth;

public interface LoginAttemptLimiter {

    void checkAllowed(String ipAddress, String username);

    void registerFailure(String ipAddress, String username);

    void registerSuccess(String ipAddress, String username);
}
