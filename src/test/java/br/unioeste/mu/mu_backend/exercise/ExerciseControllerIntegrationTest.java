package br.unioeste.mu.mu_backend.exercise;

import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
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
class ExerciseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void shouldListExercisesWithTagsWithoutLazyInitializationError() throws Exception {
        Module module = new Module();
        module.setTitle("Módulo exercícios");
        module = moduleRepository.save(module);

        Exercise exercise = new Exercise();
        exercise.setTitle("Duas Somatórias");
        exercise.setOjName("Beecrowd");
        exercise.setOjUrl("https://judge.example.com/problems/1000");
        exercise.setDifficulty(ExerciseDifficulty.EASY);
        exercise.setTags(List.of("math", "implementation"));
        exercise.setModule(module);
        exerciseRepository.save(exercise);

        mockMvc.perform(get("/api/modules/{moduleId}/exercises", module.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Duas Somatórias"))
                .andExpect(jsonPath("$[0].tags[0]").value("math"));
    }
}

