package br.unioeste.mu.mu_backend.shared.validation;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.exercise.ExerciseController;
import br.unioeste.mu.mu_backend.exercise.ExerciseDifficulty;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonController;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.material.ExtraMaterialController;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.Module;
import br.unioeste.mu.mu_backend.module.ModuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        LessonController.class,
        ExerciseController.class,
        ExtraMaterialController.class
})
@AutoConfigureMockMvc(addFilters = false)
class RequestNormalizationControllerConsistencyWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private ModuleRepository moduleRepository;

    @MockBean
    private ExerciseRepository exerciseRepository;

    @MockBean
    private ExtraMaterialRepository extraMaterialRepository;

    @Test
    void shouldTrimFieldsConsistentlyForLessonCreateAndUpdate() throws Exception {
        Module module = module(10L);
        Lesson existing = lesson(20L, module);

        when(moduleRepository.findById(10L)).thenReturn(Optional.of(module));
        when(lessonRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String payload = """
                {
                  "title": "  Aula 01  ",
                  "videoUrl": "  https://example.com/video  ",
                  "orderIndex": 1
                }
                """;

        mockMvc.perform(post("/api/modules/{moduleId}/lessons", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Aula 01"))
                .andExpect(jsonPath("$.videoUrl").value("https://example.com/video"));

        String updatePayload = """
                {
                  "title": "  Aula 02  ",
                  "videoUrl": "  https://example.com/video-2  ",
                  "orderIndex": 2
                }
                """;

        mockMvc.perform(put("/api/modules/{moduleId}/lessons/{lessonId}", 10, 20)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Aula 02"))
                .andExpect(jsonPath("$.videoUrl").value("https://example.com/video-2"));

        ArgumentCaptor<Lesson> captor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository, org.mockito.Mockito.times(2)).save(captor.capture());

        List<Lesson> saved = captor.getAllValues();
        assertThat(saved.get(0).getTitle()).isEqualTo("Aula 01");
        assertThat(saved.get(1).getTitle()).isEqualTo("Aula 02");
    }

    @Test
    void shouldTrimFieldsAndTagsConsistentlyForExerciseCreateAndUpdate() throws Exception {
        Module module = module(11L);
        Exercise existing = exercise(31L, module);

        when(moduleRepository.findById(11L)).thenReturn(Optional.of(module));
        when(exerciseRepository.findById(31L)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String payload = """
                {
                  "title": "  Soma  ",
                  "ojUrl": "  https://codeforces.com/problemset/problem/1/A  ",
                  "difficulty": "EASY",
                  "tags": ["  ad-hoc  ", "   ", " math "]
                }
                """;

        mockMvc.perform(post("/api/modules/{moduleId}/exercises", 11)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Soma"))
                .andExpect(jsonPath("$.ojUrl").value("https://codeforces.com/problemset/problem/1/A"))
                .andExpect(jsonPath("$.tags[0]").value("ad-hoc"))
                .andExpect(jsonPath("$.tags[1]").value("math"));

        String updatePayload = """
                {
                  "title": "  Soma 2  ",
                  "ojUrl": "  https://judge.beecrowd.com/pt/problems/view/1001  ",
                  "difficulty": "MEDIUM",
                  "tags": ["  implementação  ", "  strings  "]
                }
                """;

        mockMvc.perform(put("/api/modules/{moduleId}/exercises/{exerciseId}", 11, 31)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Soma 2"))
                .andExpect(jsonPath("$.ojUrl").value("https://judge.beecrowd.com/pt/problems/view/1001"))
                .andExpect(jsonPath("$.tags[0]").value("implementação"))
                .andExpect(jsonPath("$.tags[1]").value("strings"));

        ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
        verify(exerciseRepository, org.mockito.Mockito.times(2)).save(captor.capture());

        List<Exercise> saved = captor.getAllValues();
        assertThat(saved.get(0).getTags()).containsExactly("ad-hoc", "math");
        assertThat(saved.get(1).getTags()).containsExactly("implementação", "strings");
    }

    @Test
    void shouldTrimFieldsConsistentlyForExtraMaterialCreateAndUpdate() throws Exception {
        Module module = module(12L);
        ExtraMaterial existing = material(32L, module);

        when(moduleRepository.findById(12L)).thenReturn(Optional.of(module));
        when(extraMaterialRepository.findById(32L)).thenReturn(Optional.of(existing));
        when(extraMaterialRepository.save(any(ExtraMaterial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String payload = """
                {
                  "title": "  slides  ",
                  "url": "  https://example.com/slides  "
                }
                """;

        mockMvc.perform(post("/api/modules/{moduleId}/materials", 12)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("slides"))
                .andExpect(jsonPath("$.url").value("https://example.com/slides"));

        String updatePayload = """
                {
                  "title": "  artigo  ",
                  "url": "  https://example.com/article  "
                }
                """;

        mockMvc.perform(put("/api/modules/{moduleId}/materials/{materialId}", 12, 32)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("artigo"))
                .andExpect(jsonPath("$.url").value("https://example.com/article"));

        ArgumentCaptor<ExtraMaterial> captor = ArgumentCaptor.forClass(ExtraMaterial.class);
        verify(extraMaterialRepository, org.mockito.Mockito.times(2)).save(captor.capture());

        List<ExtraMaterial> saved = captor.getAllValues();
        assertThat(saved.get(0).getTitle()).isEqualTo("slides");
        assertThat(saved.get(1).getTitle()).isEqualTo("artigo");
    }

    private Module module(Long id) {
        Module module = new Module();
        ReflectionTestUtils.setField(module, "id", id);
        module.setTitle("Módulo");
        return module;
    }

    private Lesson lesson(Long id, Module module) {
        Lesson lesson = new Lesson();
        ReflectionTestUtils.setField(lesson, "id", id);
        lesson.setTitle("Aula");
        lesson.setVideoUrl("https://example.com/video");
        lesson.setOrderIndex(1);
        lesson.setModule(module);
        return lesson;
    }

    private Exercise exercise(Long id, Module module) {
        Exercise exercise = new Exercise();
        ReflectionTestUtils.setField(exercise, "id", id);
        exercise.setTitle("Exercício");
        exercise.setOjUrl("https://example.com/problem");
        exercise.setDifficulty(ExerciseDifficulty.EASY);
        exercise.setTags(List.of("tag"));
        exercise.setModule(module);
        return exercise;
    }

    private ExtraMaterial material(Long id, Module module) {
        ExtraMaterial material = new ExtraMaterial();
        ReflectionTestUtils.setField(material, "id", id);
        material.setTitle("tipo");
        material.setUrl("https://example.com/resource");
        material.setModule(module);
        return material;
    }
}
