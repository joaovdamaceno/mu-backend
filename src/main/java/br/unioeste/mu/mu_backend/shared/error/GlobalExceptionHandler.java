package br.unioeste.mu.mu_backend.shared.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Set<String> SENSITIVE_FIELD_NAMES = Set.of(
            "password", "token", "secret", "authorization", "accessToken", "refreshToken"
    );

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorCode code = mapHttpStatusToErrorCode(status);
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return buildResponse(status, code, message, request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorDetail> fieldDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldValidationDetail)
                .toList();

        List<ApiErrorDetail> objectDetails = ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> new ApiErrorDetail(
                        error.getObjectName(),
                        error.getDefaultMessage(),
                        null
                ))
                .toList();

        List<ApiErrorDetail> details = java.util.stream.Stream.concat(fieldDetails.stream(), objectDetails.stream())
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                "Falha de validação dos dados enviados.",
                request.getRequestURI(),
                details
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = ex.getConstraintViolations().stream()
                .map(violation -> new ApiErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        sanitizeRejectedValue(violation.getPropertyPath().toString(), violation.getInvalidValue())
                ))
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.VALIDATION_ERROR,
                "Falha de validação dos dados enviados.",
                request.getRequestURI(),
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);

        if (rootCause instanceof InvalidFormatException invalidFormatException) {
            String fieldPath = extractFieldPath(invalidFormatException.getPath());

            if (invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {
                String acceptedValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");

                ApiErrorDetail detail = new ApiErrorDetail(
                        fieldPath,
                        "Valor inválido para o campo '%s'. Valores aceitos: [%s].".formatted(fieldPath, acceptedValues),
                        sanitizeRejectedValue(fieldPath, invalidFormatException.getValue())
                );

                return buildResponse(
                        HttpStatus.BAD_REQUEST,
                        ErrorCode.INVALID_JSON,
                        "Corpo da requisição inválido.",
                        request.getRequestURI(),
                        List.of(detail)
                );
            }

            String targetType = invalidFormatException.getTargetType() != null
                    ? invalidFormatException.getTargetType().getSimpleName()
                    : "tipo esperado";

            ApiErrorDetail detail = new ApiErrorDetail(
                    fieldPath,
                    "Tipo incompatível para o campo '%s'. Tipo esperado: %s.".formatted(fieldPath, targetType),
                    sanitizeRejectedValue(fieldPath, invalidFormatException.getValue())
            );

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_JSON,
                    "Corpo da requisição inválido.",
                    request.getRequestURI(),
                    List.of(detail)
            );
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_JSON,
                "Corpo da requisição inválido ou JSON malformado.",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.CONFLICT,
                ErrorCode.CONFLICT,
                "Violação de integridade dos dados.",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler({AuthenticationException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ApiError> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.UNAUTHORIZED,
                "Não autorizado.",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR,
                "Ocorreu um erro inesperado.",
                request.getRequestURI(),
                List.of()
        );
    }

    private ApiErrorDetail toFieldValidationDetail(FieldError fieldError) {
        return new ApiErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                sanitizeRejectedValue(fieldError.getField(), fieldError.getRejectedValue())
        );
    }

    private Object sanitizeRejectedValue(String fieldName, Object rejectedValue) {
        if (rejectedValue == null || fieldName == null) {
            return rejectedValue;
        }

        String normalizedFieldName = fieldName.toLowerCase();
        if (SENSITIVE_FIELD_NAMES.stream().anyMatch(normalizedFieldName::contains)) {
            return null;
        }

        if (rejectedValue instanceof CharSequence textValue && textValue.length() > 120) {
            return textValue.subSequence(0, 120) + "...";
        }

        return rejectedValue;
    }

    private String extractFieldPath(List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) {
            return "corpo";
        }

        StringBuilder builder = new StringBuilder();
        for (JsonMappingException.Reference ref : path) {
            if (ref.getFieldName() != null) {
                if (!builder.isEmpty()) {
                    builder.append('.');
                }
                builder.append(ref.getFieldName());
            } else if (ref.getIndex() >= 0) {
                builder.append('[').append(ref.getIndex()).append(']');
            }
        }

        return builder.isEmpty() ? "corpo" : builder.toString();
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status,
                                                   ErrorCode code,
                                                   String message,
                                                   String path,
                                                   List<ApiErrorDetail> details) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                code.name(),
                message,
                path,
                details
        );

        return ResponseEntity.status(status).body(body);
    }

    private ErrorCode mapHttpStatusToErrorCode(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> ErrorCode.RESOURCE_NOT_FOUND;
            case UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case FORBIDDEN -> ErrorCode.FORBIDDEN;
            case CONFLICT -> ErrorCode.CONFLICT;
            case BAD_REQUEST -> ErrorCode.BAD_REQUEST;
            default -> status.is4xxClientError() ? ErrorCode.BAD_REQUEST : ErrorCode.INTERNAL_ERROR;
        };
    }
}
