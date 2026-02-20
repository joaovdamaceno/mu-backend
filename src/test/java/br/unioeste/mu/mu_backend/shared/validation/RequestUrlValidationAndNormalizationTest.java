package br.unioeste.mu.mu_backend.shared.validation;

import br.unioeste.mu.mu_backend.contest.Contest;
import br.unioeste.mu.mu_backend.contest.ContestRequest;
import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;
import br.unioeste.mu.mu_backend.exercise.ExerciseRequest;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRequest;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRequest;
import br.unioeste.mu.mu_backend.module.Module;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUrlValidationAndNormalizationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptHttpAndHttpsUrlsForAllRequests() {
        LessonRequest lessonRequest = buildValidLessonRequest();
        lessonRequest.setVideoUrl("https://example.com/video");

        ExerciseRequest exerciseRequest = buildValidExerciseRequest();
        exerciseRequest.setOjUrl("http://example.com/problem");

        ExtraMaterialRequest extraMaterialRequest = buildValidExtraMaterialRequest();
        extraMaterialRequest.setUrl("https://example.com/material");

        ContestRequest contestRequest = buildValidContestRequest();
        contestRequest.setCodeforcesMirrorUrl("https://mirror.example.com/contest");

        assertThat(validator.validate(lessonRequest)).isEmpty();
        assertThat(validator.validate(exerciseRequest)).isEmpty();
        assertThat(validator.validate(extraMaterialRequest)).isEmpty();
        assertThat(validator.validate(contestRequest)).isEmpty();
    }

    @Test
    void shouldRejectMalformedAndUnsupportedProtocolUrls() {
        LessonRequest lessonRequest = buildValidLessonRequest();
        lessonRequest.setVideoUrl("http://");

        ExerciseRequest exerciseRequest = buildValidExerciseRequest();
        exerciseRequest.setOjUrl("ftp://example.com/problem");

        ExtraMaterialRequest extraMaterialRequest = buildValidExtraMaterialRequest();
        extraMaterialRequest.setUrl("example.com/no-protocol");

        ContestRequest contestRequest = buildValidContestRequest();
        contestRequest.setCodeforcesMirrorUrl("mailto:test@example.com");

        assertThat(extractMessages(validator.validate(lessonRequest)))
                .contains("URL do vídeo deve ser válida e usar http:// ou https://");
        assertThat(extractMessages(validator.validate(exerciseRequest)))
                .contains("URL do juiz online deve ser válida e usar http:// ou https://");
        assertThat(extractMessages(validator.validate(extraMaterialRequest)))
                .contains("URL deve ser válida e usar http:// ou https://");
        assertThat(extractMessages(validator.validate(contestRequest)))
                .contains("URL do espelho Codeforces deve ser válida e usar http:// ou https://");
    }

    @Test
    void shouldTrimAndNormalizeFieldsBeforePersisting() {
        LessonRequest lessonRequest = buildValidLessonRequest();
        lessonRequest.setTitle("  Aula 01  ");
        lessonRequest.setSlug("  aula-01  ");
        lessonRequest.setSummary("  Resumo  ");
        lessonRequest.setVideoUrl("  https://example.com/video  ");

        Lesson lesson = lessonRequest.toLesson(new Module());

        assertThat(lesson.getTitle()).isEqualTo("Aula 01");
        assertThat(lesson.getSlug()).isEqualTo("aula-01");
        assertThat(lesson.getSummary()).isEqualTo("Resumo");
        assertThat(lesson.getVideoUrl()).isEqualTo("https://example.com/video");

        ExerciseRequest exerciseRequest = buildValidExerciseRequest();
        exerciseRequest.setTitle("  Soma  ");
        exerciseRequest.setOjName("  Codeforces  ");
        exerciseRequest.setOjUrl("  https://codeforces.com/problemset/problem/1/A  ");
        exerciseRequest.setTags(List.of("  ad-hoc  ", "   ", " math "));

        Exercise exercise = exerciseRequest.toExercise(new Module(), new Lesson());

        assertThat(exercise.getTitle()).isEqualTo("Soma");
        assertThat(exercise.getOjName()).isEqualTo("Codeforces");
        assertThat(exercise.getOjUrl()).isEqualTo("https://codeforces.com/problemset/problem/1/A");
        assertThat(exercise.getTags()).containsExactly("ad-hoc", "math");

        ExtraMaterialRequest extraMaterialRequest = buildValidExtraMaterialRequest();
        extraMaterialRequest.setType("  slides  ");
        extraMaterialRequest.setUrl("  https://example.com/slides  ");

        ExtraMaterial extraMaterial = extraMaterialRequest.toExtraMaterial(new Lesson());

        assertThat(extraMaterial.getType()).isEqualTo("slides");
        assertThat(extraMaterial.getUrl()).isEqualTo("https://example.com/slides");

        ContestRequest contestRequest = buildValidContestRequest();
        contestRequest.setName("  Contest 01  ");
        contestRequest.setCodeforcesMirrorUrl("   ");

        Contest contest = new Contest();
        contestRequest.applyTo(contest);

        assertThat(contest.getName()).isEqualTo("Contest 01");
        assertThat(contest.getCodeforcesMirrorUrl()).isNull();
    }

    @Test
    void shouldKeepNotBlankMessageForWhitespaceOnlyRequiredUrls() {
        LessonRequest lessonRequest = buildValidLessonRequest();
        lessonRequest.setVideoUrl("   ");

        assertThat(extractMessages(validator.validate(lessonRequest)))
                .contains("URL do vídeo é obrigatória");
    }

    private static Set<String> extractMessages(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    private LessonRequest buildValidLessonRequest() {
        LessonRequest request = new LessonRequest();
        request.setTitle("Aula");
        request.setSlug("aula");
        request.setSummary("Resumo");
        request.setVideoUrl("https://example.com/video");
        request.setOrderIndex(1);
        return request;
    }

    private ExerciseRequest buildValidExerciseRequest() {
        ExerciseRequest request = new ExerciseRequest();
        request.setTitle("Exercício");
        request.setOjName("Codeforces");
        request.setOjUrl("https://codeforces.com/problemset/problem/1/A");
        request.setDifficulty(ExerciseDifficulty.EASY);
        request.setTags(List.of("math"));
        return request;
    }

    private ExtraMaterialRequest buildValidExtraMaterialRequest() {
        ExtraMaterialRequest request = new ExtraMaterialRequest();
        request.setType("video");
        request.setUrl("https://example.com/material");
        return request;
    }

    private ContestRequest buildValidContestRequest() {
        ContestRequest request = new ContestRequest();
        request.setName("Contest");
        request.setDurationMinutes(120);
        request.setStartDateTime(LocalDateTime.now().plusDays(1));
        request.setCodeforcesMirrorUrl("https://mirror.example.com/contest");
        return request;
    }
}
