package com.example.cligenerator.controller;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import com.example.cligenerator.service.EntityGenerationService;
import com.example.cligenerator.service.ProjectGenerationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ProjectGenerationController {
    private final EntityGenerationService entityGenerationService;
    private final ProjectGenerationService projectGenerationService;

    public ProjectGenerationController(EntityGenerationService entityGenerationService, ProjectGenerationService projectGenerationService) {
        this.entityGenerationService = entityGenerationService;
        this.projectGenerationService = projectGenerationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateProject(@RequestBody ProjectDescription description) throws JsonProcessingException, GenerationException {
        if (!description.getEntitiesDescription().isEmpty()) {
            description.setSupportingAI(true);
            String entitiesJson = entityGenerationService.generate(description);
            ObjectMapper mapper = new ObjectMapper();
            description.setEntities(mapper.readValue(entitiesJson, new TypeReference<List<EntityDefinition>>() {}));
        }
        System.out.println("Recieved:");
        System.out.println(description.getProjectName());
        System.out.println(description.getGroupId());
        System.out.println(description.getArtifactId());
        System.out.println(description.getPackageName());
        System.out.println(description.getJavaVersion());
        System.out.println(description.getSpringBootVersion());
        System.out.println(description.getDependencies());
        System.out.println(description.getOutputDir());
        System.out.println(description.getBuildTool());
        System.out.println(description.getDatabaseConfig());

        System.out.println("- entities: " + description.getEntities());
        System.out.println("- description:  " + description.getEntitiesDescription());
        System.out.println("- ai: " + description.isSupportingAI());
        System.out.println("- docker: " + description.isIncludesDocker());
        System.out.println("- git: " + description.isIncludesGit());
        System.out.println("- github: " + description.getGithubRemoteUrl());
        System.out.println("- tests: " + description.isIncludesTests());
        System.out.println("- gitlab: " + description.isIncludesGitlab());
        System.out.println("- swagger: " + description.isIncludesSwagger());

        projectGenerationService.generateProject(description);
        return ResponseEntity.ok("Project parsed and received successfully.");
    }
}
