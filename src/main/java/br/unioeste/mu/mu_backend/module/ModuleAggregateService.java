package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.exercise.Exercise;
import br.unioeste.mu.mu_backend.exercise.ExerciseRepository;
import br.unioeste.mu.mu_backend.lesson.Lesson;
import br.unioeste.mu.mu_backend.lesson.LessonRepository;
import br.unioeste.mu.mu_backend.material.ExtraMaterial;
import br.unioeste.mu.mu_backend.material.ExtraMaterialRepository;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ExerciseAggregateResponse;
import br.unioeste.mu.mu_backend.module.aggregate.ExtraMaterialAggregateResponse;
import br.unioeste.mu.mu_backend.module.aggregate.LessonAggregateResponse;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import br.unioeste.mu.mu_backend.shared.error.domain.BusinessValidationException;
import br.unioeste.mu.mu_backend.shared.error.domain.ConflictException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ModuleAggregateService {

    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExtraMaterialRepository extraMaterialRepository;

    public ModuleAggregateService(
            ModuleRepository moduleRepository,
            LessonRepository lessonRepository,
            ExerciseRepository exerciseRepository,
            ExtraMaterialRepository extraMaterialRepository
    ) {
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
        this.exerciseRepository = exerciseRepository;
        this.extraMaterialRepository = extraMaterialRepository;
    }

    @Transactional(readOnly = true)
    public List<ModuleAggregateResponse> listAllFullModules() {
        List<Module> modules = moduleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (modules.isEmpty()) {
            return List.of();
        }

        List<Long> moduleIds = modules.stream().map(Module::getId).toList();

        Map<Long, List<LessonAggregateResponse>> lessonsByModule = new HashMap<>();
        lessonRepository.findByModuleIdInOrderByModuleIdAscOrderIndexAsc(moduleIds)
                .forEach(lesson -> lessonsByModule
                        .computeIfAbsent(lesson.getModule().getId(), ignored -> new ArrayList<>())
                        .add(ModuleAggregateResponse.lessonFrom(lesson)));

        Map<Long, List<ExerciseAggregateResponse>> exercisesByModule = new HashMap<>();
        exerciseRepository.findByModuleIdInOrderByModuleIdAscIdAsc(moduleIds)
                .forEach(exercise -> exercisesByModule
                        .computeIfAbsent(exercise.getModule().getId(), ignored -> new ArrayList<>())
                        .add(ModuleAggregateResponse.exerciseFrom(exercise)));

        Map<Long, List<ExtraMaterialAggregateResponse>> extraMaterialsByModule = new HashMap<>();
        extraMaterialRepository.findByModuleIdInOrderByModuleIdAscIdAsc(moduleIds)
                .forEach(extraMaterial -> extraMaterialsByModule
                        .computeIfAbsent(extraMaterial.getModule().getId(), ignored -> new ArrayList<>())
                        .add(ModuleAggregateResponse.extraMaterialFrom(extraMaterial)));

        return modules.stream()
                .map(module -> new ModuleAggregateResponse(
                        module,
                        lessonsByModule.getOrDefault(module.getId(), List.of()),
                        exercisesByModule.getOrDefault(module.getId(), List.of()),
                        extraMaterialsByModule.getOrDefault(module.getId(), List.of())
                ))
                .toList();
    }

    @Transactional
    public ModuleAggregateResponse createFullModule(ModuleAggregateRequest request) {
        validateAggregatePayload(request);

        Module module = new Module();
        module.setTitle(request.getTitle());
        module.setNotes(request.getNotes());
        module.setPublished(request.isPublished());

        Module persistedModule = moduleRepository.save(module);

        List<LessonAggregateResponse> lessonResponses = request.getLessons().stream()
                .map(lessonRequest -> {
                    Lesson lesson = new Lesson();
                    lesson.setTitle(lessonRequest.getTitle());
                    lesson.setSlug(lessonRequest.getSlug());
                    lesson.setSummary(lessonRequest.getSummary());
                    lesson.setVideoUrl(lessonRequest.getVideoUrl());
                    lesson.setOrderIndex(lessonRequest.getOrderIndex());
                    lesson.setModule(persistedModule);
                    return lessonRepository.save(lesson);
                })
                .map(ModuleAggregateResponse::lessonFrom)
                .toList();

        List<Exercise> persistedExercises = request.getExercises().stream()
                .map(exerciseRequest -> exerciseRequest.toExercise(persistedModule))
                .map(exerciseRepository::save)
                .toList();

        List<ExtraMaterial> persistedExtraMaterials = request.getExtraMaterials().stream()
                .map(extraMaterialRequest -> extraMaterialRequest.toExtraMaterial(persistedModule))
                .map(extraMaterialRepository::save)
                .toList();

        return new ModuleAggregateResponse(
                persistedModule,
                lessonResponses,
                persistedExercises.stream().map(ModuleAggregateResponse::exerciseFrom).toList(),
                persistedExtraMaterials.stream().map(ModuleAggregateResponse::extraMaterialFrom).toList()
        );
    }

    private void validateAggregatePayload(ModuleAggregateRequest request) {
        if (request == null) {
            throw invalidPayload("Payload do módulo agregado é obrigatório");
        }

        Set<Integer> usedOrderIndexes = new HashSet<>();
        Set<String> usedSlugs = new HashSet<>();

        for (int lessonIndex = 0; lessonIndex < request.getLessons().size(); lessonIndex++) {
            LessonAggregateRequest lesson = request.getLessons().get(lessonIndex);
            int lessonPosition = lessonIndex + 1;

            if (lesson == null) {
                throw invalidPayload("Lição na posição " + lessonPosition + " está ausente");
            }

            validateRequiredText(lesson.getSlug(), "slug", lessonPosition);

            Integer orderIndex = lesson.getOrderIndex();
            if (orderIndex == null) {
                throw invalidPayload("Lição na posição " + lessonPosition + " deve informar orderIndex");
            }

            if (!usedOrderIndexes.add(orderIndex)) {
                throw duplicatedResource("orderIndex duplicado para lições do módulo: " + orderIndex);
            }

            String normalizedSlug = lesson.getSlug().trim().toLowerCase();
            if (!usedSlugs.add(normalizedSlug)) {
                throw duplicatedResource("slug duplicado para lições do módulo: " + lesson.getSlug().trim());
            }
        }
    }

    private void validateRequiredText(String value, String fieldName, int lessonPosition) {
        if (value == null || value.trim().isEmpty()) {
            throw invalidPayload("Lição na posição " + lessonPosition + " possui " + fieldName + " inválido");
        }
    }

    private BusinessValidationException invalidPayload(String message) {
        return new BusinessValidationException(message);
    }

    private ConflictException duplicatedResource(String message) {
        return new ConflictException(message);
    }
}
