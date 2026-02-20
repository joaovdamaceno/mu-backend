package br.unioeste.mu.mu_backend.module;

import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateRequest;
import br.unioeste.mu.mu_backend.module.aggregate.ModuleAggregateResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/modules/full", "/api/v2/modules/full"})
@Tag(name = "module-controller")
@CrossOrigin(
        origins = {"${app.cors.allowed-origins[0]}", "${app.cors.allowed-origins[1]}"},
        methods = {RequestMethod.POST}
)
public class ModuleAggregateController {

    private final ModuleAggregateService moduleAggregateService;

    public ModuleAggregateController(ModuleAggregateService moduleAggregateService) {
        this.moduleAggregateService = moduleAggregateService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create module with lessons, exercises, and extra materials in a single request",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload para criação agregada de módulo",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "title": "Introdução à Programação",
                              "notes": "Módulo inicial para novos alunos",
                              "published": true,
                              "lessons": [
                                {
                                  "title": "Primeiros passos",
                                  "slug": "primeiros-passos",
                                  "summary": "Conceitos básicos",
                                  "videoUrl": "https://example.com/video/1",
                                  "orderIndex": 1,
                                  "exercises": [
                                    {
                                      "title": "Exercício de variáveis",
                                      "ojName": "Beecrowd",
                                      "ojUrl": "https://judge.example.com/problems/1001",
                                      "difficulty": "EASY",
                                      "tags": ["variaveis", "entrada-saida"]
                                    }
                                  ],
                                  "extraMaterials": [
                                    {
                                      "type": "ARTICLE",
                                      "url": "https://example.com/material/variaveis"
                                    }
                                  ]
                                }
                              ]
                            }
                            """))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Module and children created", content = @Content(
                    examples = @ExampleObject(value = """
                            {
                              "module": {
                                "id": 101,
                                "title": "Introdução à Programação",
                                "notes": "Módulo inicial para novos alunos",
                                "published": true
                              },
                              "lessons": [
                                {
                                  "id": 501,
                                  "title": "Primeiros passos",
                                  "slug": "primeiros-passos",
                                  "summary": "Conceitos básicos",
                                  "videoUrl": "https://example.com/video/1",
                                  "orderIndex": 1,
                                  "exercises": [
                                    {
                                      "id": 901,
                                      "title": "Exercício de variáveis",
                                      "ojName": "Beecrowd",
                                      "ojUrl": "https://judge.example.com/problems/1001",
                                      "difficulty": "EASY",
                                      "tags": ["variaveis", "entrada-saida"],
                                      "moduleId": 101,
                                      "lessonId": 501
                                    }
                                  ],
                                  "extraMaterials": [
                                    {
                                      "id": 1201,
                                      "type": "ARTICLE",
                                      "url": "https://example.com/material/variaveis",
                                      "lessonId": 501
                                    }
                                  ]
                                }
                              ]
                            }
                            """
                    ))),
            @ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    public ModuleAggregateResponse create(@Valid @org.springframework.web.bind.annotation.RequestBody ModuleAggregateRequest request) {
        return moduleAggregateService.createFullModule(request);
    }
}
