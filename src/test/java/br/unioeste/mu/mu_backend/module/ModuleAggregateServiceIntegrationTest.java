package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.aggregate.ExerciseAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "jwt.secret=0123456789abcdef0123456789abcdef")
class ModuleAggregateServiceIntegrationTest {

    @Autowired
    private ModuleAggregateService moduleAggregateService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExtraMaterialRepository extraMaterialRepository;

    @Test
    void shouldRejectDuplicatedOrderIndexAndKeepDatabaseUnchanged() {
        long modulesBefore = moduleRepository.count();
        long lessonsBefore = lessonRepository.count();
        long exercisesBefore = exerciseRepository.count();
        long extraMaterialsBefore = extraMaterialRepository.count();

        ModuleAggregateRequest request = new ModuleAggregateRequest();
        request.setTitle("Módulo de teste");
        request.setLessons(List.of(
                buildLesson("Primeira lição", "intro", 1),
                buildLesson("Segunda lição", "avancado", 1)
        ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> moduleAggregateService.createFullModule(request)
        );

        assertEquals("orderIndex duplicado para lições do módulo: 1", exception.getReason());
        assertEquals(modulesBefore, moduleRepository.count());
        assertEquals(lessonsBefore, lessonRepository.count());
        assertEquals(exercisesBefore, exerciseRepository.count());
        assertEquals(extraMaterialsBefore, extraMaterialRepository.count());
    }

    @Test
    void shouldReturnAggregateResponseWithPersistedIds() {
        ModuleAggregateRequest request = new ModuleAggregateRequest();
        request.setTitle("Módulo agregado");
        request.setNotes("Notas do módulo");

        LessonAggregateRequest lesson = buildLesson("Lição única", "licao-unica", 1);
        request.setLessons(List.of(lesson));

        ModuleAggregateResponse response = moduleAggregateService.createFullModule(request);

        assertNotNull(response.getModule().getId());
        assertEquals("Módulo agregado", response.getModule().getTitle());
        assertEquals(1, response.getLessons().size());
        assertNotNull(response.getLessons().get(0).getId());
        assertNotNull(response.getLessons().get(0).getExercises().get(0).getId());
    }

    private LessonAggregateRequest buildLesson(String title, String slug, Integer orderIndex) {
        LessonAggregateRequest lesson = new LessonAggregateRequest();
        lesson.setTitle(title);
        lesson.setSlug(slug);
        lesson.setSummary("Resumo");
        lesson.setVideoUrl("https://example.com/video");
        lesson.setOrderIndex(orderIndex);

        ExerciseAggregateRequest exercise = new ExerciseAggregateRequest();
        exercise.setTitle("Exercício 1");
        exercise.setOjName("Beecrowd");
        exercise.setOjUrl("https://judge.example.com/problems/1");
        exercise.setDifficulty(ExerciseDifficulty.EASY);

        lesson.setExercises(List.of(exercise));
        return lesson;
    }
}
