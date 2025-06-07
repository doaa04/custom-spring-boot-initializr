package com.example.cligenerator.controller;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import com.example.cligenerator.service.EntityGenerationService;
import com.example.cligenerator.service.ProjectGenerationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public ResponseEntity<byte[]> generateProject(@RequestBody ProjectDescription description) throws JsonProcessingException, GenerationException {
        if (!description.getEntitiesDescription().isEmpty()) {
            description.setSupportingAI(true);
            String entitiesJson = entityGenerationService.generate(description);
            ObjectMapper mapper = new ObjectMapper();
            description.setEntities(mapper.readValue(entitiesJson, new TypeReference<List<EntityDefinition>>() {}));
        }

        Path projectBasePath;
        try {
            projectBasePath = projectGenerationService.generateProject(description);
            Path zipPath = Files.createTempFile("project-", ".zip");
            zipDirectory(projectBasePath, zipPath);
            byte[] zipBytes = Files.readAllBytes(zipPath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(projectBasePath.getFileName().toString() + ".zip")
                    .build());
            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void zipDirectory(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to zip file: " + path, e);
                        }
                    });
        }
    }
}