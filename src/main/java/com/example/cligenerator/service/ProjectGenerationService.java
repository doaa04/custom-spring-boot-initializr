package com.example.cligenerator.service;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.DatabaseConfig;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ProjectGenerationService {
    private final InitializrService initializrService;
    private final CodeGeneratorService codeGeneratorService;
    private final ApiDocumentationService apiDocumentationService;
    private final DockerService dockerService;
    private final GitlabService gitlabService;
    private final TestGenerationService testGenerationService;
    private final GitService gitService;

    public ProjectGenerationService(InitializrService initializrService, CodeGeneratorService codeGeneratorService, ApiDocumentationService apiDocumentationService, DockerService dockerService, GitlabService gitlabService, TestGenerationService testGenerationService, GitService gitService) {
        this.initializrService = initializrService;
        this.codeGeneratorService = codeGeneratorService;
        this.apiDocumentationService = apiDocumentationService;
        this.dockerService = dockerService;
        this.gitlabService = gitlabService;
        this.testGenerationService = testGenerationService;
        this.gitService = gitService;
    }

    public Path generateProject(ProjectDescription description) throws GenerationException {
        String projectName = description.getProjectName();
        String packageName = description.getPackageName();
        String artifactId = description.getArtifactId();
        String javaVersion = description.getJavaVersion();
        DatabaseConfig databaseConfig = description.getDatabaseConfig();
        List<EntityDefinition> entityDefinitions = description.getEntities();
        boolean generateOpenApi = description.isIncludesSwagger();
        boolean includeGitlab = description.isIncludesGitlab();
        boolean includeDocker = description.isIncludesDocker();
        boolean includeTests = description.isIncludesTests();
        boolean pushToGit = description.isIncludesGit();
        String depList = String.join(",", description.getDependencies());

        Path projectBasePath;
        try {
            projectBasePath = initializrService.downloadAndUnzipProject(
                    projectName, description.getGroupId(), artifactId, packageName,
                    description.getJavaVersion(), description.getSpringBootVersion(), depList,
                    description.getBuildTool(), description.getOutputDir());
        } catch (Exception e) {
            throw new GenerationException("Spring Initializr", "Failed to generate base project",
                    "Error downloading from Spring Initializr: " + e.getMessage(), e);
        }

        // Find the actual package where the main class was created
        String actualPackageName = findMainClassPackage(projectBasePath, artifactId);
        if (actualPackageName != null) {
            packageName = actualPackageName;
            System.out.println("Using detected package: " + packageName);
        }

        try {
            // Generate application.properties with database configuration
            codeGeneratorService.generateApplicationProperties(projectBasePath, databaseConfig);

            // Generate entity code
            for (EntityDefinition entityDef : entityDefinitions) {
                //logger.info("Generating code for entity: {}", entityDef.getName());
                codeGeneratorService.generateCode(projectBasePath, packageName, entityDef);
            } // Generate comprehensive API documentation if requested
            if (generateOpenApi) {
                System.out.println("Adding OpenAPI dependency...");
                codeGeneratorService.addOpenApiDependency(projectBasePath, description);
                System.out.println("Generating comprehensive API documentation...");
                apiDocumentationService.generateApiDocumentation(projectBasePath, packageName, projectName,
                        entityDefinitions);
            }

            // Generate Docker configuration if requested
            if (includeDocker) {
                System.out.println("Generating Docker configuration...");
                dockerService.generateDockerConfiguration(projectBasePath, artifactId, javaVersion, databaseConfig);
            }

            // Generate Gitlab configuration if requested
            if (includeGitlab) {
                System.out.println("Generating Gitlab configuration...");
                gitlabService.generateGitlabConfiguration(projectBasePath, description);
            }

            // Generate tests if requested
            if (includeTests) {
                System.out.println("Generating comprehensive tests...");
                for (EntityDefinition entityDef : entityDefinitions) {
                    testGenerationService.generateTestsForEntity(projectBasePath, packageName, entityDef);
                }
            }

            if (pushToGit) {
                System.out.println("Setting up GitHub repository...");
                gitService.initPush(projectBasePath, description);
            }



        } catch (Exception e) {
            throw new GenerationException("Code Generation", "Failed to generate project files",
                    "Error during code generation: " + e.getMessage(), e);
        }
        return projectBasePath;
    }

    private String findMainClassPackage(Path projectBasePath, String artifactId) {
        try {
            String mainClassName = toCamelCase(artifactId) + "Application";
            Path srcPath = projectBasePath.resolve("src/main/java");

            return Files.walk(srcPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(mainClassName + ".java"))
                    .findFirst()
                    .map(path -> {
                        Path relativePath = srcPath.relativize(path.getParent());
                        return relativePath.toString().replace('/', '.').replace('\\', '.');
                    })
                    .orElse(null);
        } catch (Exception e) {
            System.out.println("Could not detect main class package, using provided package name");
            return null;
        }
    }

    private String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
