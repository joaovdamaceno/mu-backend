package br.unioeste.mu.mu_backend.shared.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setRequestURI("/modules/aggregate");
        request = mockHttpServletRequest;
    }

    @Test
    void shouldReturnStructuredDetailsForFieldAndObjectErrors() throws Exception {
        DummyPayload payload = new DummyPayload();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(payload, "dummyPayload");
        bindingResult.addError(new FieldError("dummyPayload", "lessons[0].slug", null, false, null, null, "Slug é obrigatório"));
        bindingResult.addError(new ObjectError("dummyPayload", "Payload inválido"));

        Method method = DummyController.class.getDeclaredMethod("create", DummyPayload.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiError> response = handler.handleMethodArgumentNotValid(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Falha de validação dos dados enviados.");
        assertThat(response.getBody().details()).hasSize(2);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("lessons[0].slug");
        assertThat(response.getBody().details().get(0).message()).isEqualTo("Slug é obrigatório");
        assertThat(response.getBody().details().get(0).rejectedValue()).isNull();
        assertThat(response.getBody().details().get(1).field()).isEqualTo("dummyPayload");
        assertThat(response.getBody().details().get(1).message()).isEqualTo("Payload inválido");
    }

    @Test
    void shouldReturnObjectiveMessageWhenEnumValueIsInvalid() {
        InvalidFormatException cause = InvalidFormatException.from(null, "Enum inválido", "VERY_HARD", DummyDifficulty.class);
        cause.prependPath(new Object(), "difficulty");

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON inválido", cause);

        ResponseEntity<ApiError> response = handler.handleHttpMessageNotReadable(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().details()).hasSize(1);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("difficulty");
        assertThat(response.getBody().details().get(0).message())
                .isEqualTo("Valor inválido para o campo 'difficulty'. Valores aceitos: [EASY, MEDIUM, HARD].");
        assertThat(response.getBody().details().get(0).rejectedValue()).isEqualTo("VERY_HARD");
    }

    @Test
    void shouldReturnObjectiveMessageWhenTypeIsIncompatible() {
        InvalidFormatException cause = InvalidFormatException.from(null, "Tipo inválido", "abc", Integer.class);
        cause.prependPath(new Object(), "orderIndex");

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("JSON inválido", cause);

        ResponseEntity<ApiError> response = handler.handleHttpMessageNotReadable(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().details()).hasSize(1);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("orderIndex");
        assertThat(response.getBody().details().get(0).message())
                .isEqualTo("Tipo incompatível para o campo 'orderIndex'. Tipo esperado: Integer.");
        assertThat(response.getBody().details().get(0).rejectedValue()).isEqualTo("abc");
    }

    @Test
    void shouldMapKnownConstraintToFriendlyConflictMessage() {
        RuntimeException cause = new RuntimeException("ERROR: duplicate key value violates unique constraint \"registrations_email_unique\"");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Violação", cause);

        ResponseEntity<ApiError> response = handler.handleDataIntegrityViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("CONFLICT");
        assertThat(response.getBody().message()).isEqualTo("email já cadastrado");
        assertThat(response.getBody().details()).hasSize(2);
        assertThat(response.getBody().details().get(0).field()).isEqualTo("constraint");
        assertThat(response.getBody().details().get(0).rejectedValue()).isEqualTo("registrations_email_unique");
        assertThat(response.getBody().details().get(1).field()).isEqualTo("field");
        assertThat(response.getBody().details().get(1).rejectedValue()).isEqualTo("email");
    }

    @Test
    void shouldReturnSafeFallbackWhenConstraintCannotBeIdentified() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Violação", new RuntimeException("erro genérico"));

        ResponseEntity<ApiError> response = handler.handleDataIntegrityViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("CONFLICT");
        assertThat(response.getBody().message()).isEqualTo("Violação de integridade dos dados.");
        assertThat(response.getBody().details()).isEmpty();
    }

    private static class DummyController {
        public void create(DummyPayload payload) {
        }
    }

    private static class DummyPayload {
    }

    private enum DummyDifficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
