package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.aggregate.ExerciseAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ExtraMaterialAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import br.unioeste.mu.mu_backend.shared.error.domain.ConflictException;
import br.unioeste.mu.mu_backend.shared.error.domain.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> moduleAggregateService.createFullModule(request)
        );

        assertEquals("orderIndex duplicado para lições do módulo: 1", exception.getMessage());
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

        request.setLessons(List.of(buildLesson("Lição única", "licao-unica", 1)));
        request.setExercises(List.of(buildExercise()));

        ModuleAggregateResponse response = moduleAggregateService.createFullModule(request);

        assertNotNull(response.getModule().getId());
        assertEquals("Módulo agregado", response.getModule().getTitle());
        assertEquals(1, response.getLessons().size());
        assertNotNull(response.getLessons().get(0).getId());
        assertEquals(1, response.getExercises().size());
        assertNotNull(response.getExercises().get(0).getId());
    }



    @Test
    void shouldReplaceNestedCollectionsWhenUpdatingFullModule() {
        ModuleAggregateRequest createRequest = new ModuleAggregateRequest();
        createRequest.setTitle("Módulo para atualização");
        createRequest.setNotes("Notas antigas");
        createRequest.setPublished(true);
        createRequest.setLessons(List.of(
                buildLesson("Lição antiga 1", "licao-antiga-1", 1),
                buildLesson("Lição antiga 2", "licao-antiga-2", 2)
        ));
        createRequest.setExercises(List.of(buildExercise("Exercício antigo")));
        createRequest.setExtraMaterials(List.of(buildMaterial("Material antigo", "https://example.com/old")));

        ModuleAggregateResponse created = moduleAggregateService.createFullModule(createRequest);

        ModuleAggregateRequest updateRequest = new ModuleAggregateRequest();
        updateRequest.setTitle("Módulo atualizado");
        updateRequest.setNotes("Notas novas");
        updateRequest.setPublished(false);
        updateRequest.setLessons(List.of(buildLesson("Lição nova", "licao-nova", 1)));
        updateRequest.setExercises(List.of(buildExercise("Exercício novo")));
        updateRequest.setExtraMaterials(List.of(buildMaterial("Material novo", "https://example.com/new")));

        ModuleAggregateResponse updated = moduleAggregateService.updateFullModule(created.getModule().getId(), updateRequest);

        Module persistedModule = moduleRepository.findById(created.getModule().getId()).orElseThrow();

        assertEquals("Módulo atualizado", updated.getModule().getTitle());
        assertEquals("Notas novas", updated.getModule().getNotes());
        assertEquals(false, updated.getModule().isPublished());

        assertEquals(1, lessonRepository.findByModuleOrderByOrderIndexAsc(persistedModule).size());
        assertEquals("Lição nova", lessonRepository.findByModuleOrderByOrderIndexAsc(persistedModule).get(0).getTitle());

        assertEquals(1, exerciseRepository.findByModule(persistedModule).size());
        assertEquals("Exercício novo", exerciseRepository.findByModule(persistedModule).get(0).getTitle());

        assertEquals(1, extraMaterialRepository.findByModule(persistedModule).size());
        assertEquals("Material novo", extraMaterialRepository.findByModule(persistedModule).get(0).getTitle());
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingUnknownModule() {
        ModuleAggregateRequest request = new ModuleAggregateRequest();
        request.setTitle("Inexistente");
        request.setLessons(List.of(buildLesson("Lição", "slug", 1)));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> moduleAggregateService.updateFullModule(999_999L, request)
        );

        assertEquals("Módulo não encontrado para id=999999", exception.getMessage());
    }

    @Test
    void shouldListAllFullModulesWithNestedData() {
        long modulesBefore = moduleRepository.count();

        ModuleAggregateRequest firstRequest = new ModuleAggregateRequest();
        firstRequest.setTitle("Módulo A");
        firstRequest.setLessons(List.of(buildLesson("Lição A1", "licao-a1", 1)));
        firstRequest.setExercises(List.of(buildExercise()));
        moduleAggregateService.createFullModule(firstRequest);

        ModuleAggregateRequest secondRequest = new ModuleAggregateRequest();
        secondRequest.setTitle("Módulo B");
        secondRequest.setLessons(List.of(buildLesson("Lição B1", "licao-b1", 1)));
        moduleAggregateService.createFullModule(secondRequest);

        List<ModuleAggregateResponse> modules = moduleAggregateService.listAllFullModules();

        assertEquals(modulesBefore + 2, modules.size());

        ModuleAggregateResponse firstCreated = modules.get((int) modulesBefore);
        ModuleAggregateResponse secondCreated = modules.get((int) modulesBefore + 1);

        assertEquals("Módulo A", firstCreated.getModule().getTitle());
        assertEquals(1, firstCreated.getLessons().size());
        assertEquals(1, firstCreated.getExercises().size());
        assertEquals("Módulo B", secondCreated.getModule().getTitle());
        assertEquals(1, secondCreated.getLessons().size());
    }

    private LessonAggregateRequest buildLesson(String title, String slug, Integer orderIndex) {
        LessonAggregateRequest lesson = new LessonAggregateRequest();
        lesson.setTitle(title);
        lesson.setVideoUrl("https://example.com/video");
        lesson.setOrderIndex(orderIndex);
        return lesson;
    }

    private ExerciseAggregateRequest buildExercise() {
        return buildExercise("Exercício 1");
    }

    private ExerciseAggregateRequest buildExercise(String title) {
        ExerciseAggregateRequest exercise = new ExerciseAggregateRequest();
        exercise.setTitle(title);
        exercise.setOjUrl("https://judge.example.com/problems/1");
        exercise.setDifficulty(ExerciseDifficulty.EASY);
        return exercise;
    }

    private ExtraMaterialAggregateRequest buildMaterial(String title, String url) {
        ExtraMaterialAggregateRequest material = new ExtraMaterialAggregateRequest();
        material.setTitle(title);
        material.setUrl(url);
        return material;
    }
}
