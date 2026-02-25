package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;
import br.unioeste.mu.mu_backend.module.aggregate.ExerciseAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "jwt.secret=0123456789abcdef0123456789abcdef")
@AutoConfigureMockMvc
class ModuleControllerFullEndpointIntegrationTest {

    @Autowired
    private ModuleAggregateService moduleAggregateService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnFullModulesWithoutLazyInitializationErrors() throws Exception {
        ModuleAggregateRequest request = new ModuleAggregateRequest();
        request.setTitle("Módulo endpoint full");
        request.setLessons(List.of(buildLesson()));
        request.setExercises(List.of(buildExerciseWithTags()));

        moduleAggregateService.createFullModule(request);

        mockMvc.perform(get("/api/modules/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.module.title == 'Módulo endpoint full')].exercises[0].tags[0]").value("arrays"));
    }

    private LessonAggregateRequest buildLesson() {
        LessonAggregateRequest lesson = new LessonAggregateRequest();
        lesson.setTitle("Lição");
        lesson.setVideoUrl("https://example.com/video");
        lesson.setOrderIndex(1);
        return lesson;
    }

    private ExerciseAggregateRequest buildExerciseWithTags() {
        ExerciseAggregateRequest exercise = new ExerciseAggregateRequest();
        exercise.setTitle("Exercício");
        exercise.setOjUrl("https://judge.example.com/problems/10");
        exercise.setDifficulty(ExerciseDifficulty.MEDIUM);
        exercise.setTags(List.of("arrays", "sorting"));
        return exercise;
    }
}
