package br.unioeste.mu.mu_backend.shared.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<ApiErrorDetail> details
) {
}
