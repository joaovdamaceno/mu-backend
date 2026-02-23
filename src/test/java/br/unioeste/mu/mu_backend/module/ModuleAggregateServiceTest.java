package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleAggregateServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExtraMaterialRepository extraMaterialRepository;

    @InjectMocks
    private ModuleAggregateService moduleAggregateService;

    @Test
    void shouldIgnoreNestedRecordsWithoutModuleInsteadOfFailing() {
        Module module = new Module();
        module.setTitle("Módulo");

        Lesson lessonWithoutModule = new Lesson();
        lessonWithoutModule.setTitle("Lição sem módulo");
        lessonWithoutModule.setSlug("l1");
        lessonWithoutModule.setSummary("Resumo");
        lessonWithoutModule.setVideoUrl("https://example.com/video");
        lessonWithoutModule.setOrderIndex(1);

        Exercise exerciseWithoutModule = new Exercise();
        exerciseWithoutModule.setTitle("Exercício sem módulo");

        ExtraMaterial materialWithoutModule = new ExtraMaterial();
        materialWithoutModule.setType("Artigo");
        materialWithoutModule.setUrl("https://example.com/material");

        when(moduleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(module));
        when(lessonRepository.findByModuleIdInOrderByModuleIdAscOrderIndexAsc(List.of(null)))
                .thenReturn(List.of(lessonWithoutModule));
        when(exerciseRepository.findByModuleIdInOrderByModuleIdAscIdAsc(List.of(null)))
                .thenReturn(List.of(exerciseWithoutModule));
        when(extraMaterialRepository.findByModuleIdInOrderByModuleIdAscIdAsc(List.of(null)))
                .thenReturn(List.of(materialWithoutModule));

        List<ModuleAggregateResponse> response = moduleAggregateService.listAllFullModules();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getLessons()).isEmpty();
        assertThat(response.getFirst().getExercises()).isEmpty();
        assertThat(response.getFirst().getExtraMaterials()).isEmpty();
    }
}
