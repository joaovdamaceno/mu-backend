package br.unioeste.mu.mu_backend.shared.error;

public enum ErrorCode {
    RESOURCE_NOT_FOUND,
    BUSINESS_VALIDATION,
    VALIDATION_ERROR,
    INVALID_JSON,
    CONFLICT,
    UNAUTHORIZED,
    FORBIDDEN,
    LOGIN_RATE_LIMIT_EXCEEDED,
    INTERNAL_ERROR,
    BAD_REQUEST
}
