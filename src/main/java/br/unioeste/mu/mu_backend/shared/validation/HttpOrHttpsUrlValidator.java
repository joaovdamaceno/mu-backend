package br.unioeste.mu.mu_backend.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpOrHttpsUrlValidator implements ConstraintValidator<HttpOrHttpsUrl, String> {

    private boolean allowBlank;

    @Override
    public void initialize(HttpOrHttpsUrl constraintAnnotation) {
        this.allowBlank = constraintAnnotation.allowBlank();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return allowBlank;
        }

        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            boolean validScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
            boolean hasHost = host != null && !host.isBlank();

            return uri.isAbsolute() && validScheme && hasHost;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
