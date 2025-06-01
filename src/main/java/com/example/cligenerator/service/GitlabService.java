package com.example.cligenerator.service;

import com.example.cligenerator.model.ProjectDescription;
import org.apache.tools.ant.Project;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitlabService {
    private final TemplateService templateService;

    public GitlabService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public void generateGitlabConfiguration(Path projectPath, ProjectDescription description) {
        String artifactId = description.getArtifactId();
        String javaVersion = description.getJavaVersion();
        boolean isGradle = description.getBuildTool().equals("gradle");

        String image = String.format("openjdk:%s", javaVersion);

        StringBuilder content = new StringBuilder();
        content.append("image: ").append(image).append("\n\n");

        content.append("stages:\n")
                .append("  - build\n")
                .append("  - test\n")
                .append("  - package\n\n");

        if (isGradle) {
            content.append("variables:\n")
                    .append("  GRADLE_OPTS: \"-Dorg.gradle.daemon=false\"\n\n");
        }

        // Build job
        content.append("build-job:\n")
                .append("  stage: build\n")
                .append("  script:\n");

        if (isGradle) {
            content.append("    - ./gradlew build\n");
        } else {
            content.append("    - mvn clean compile\n");
        }

        content.append("\n");

        // Test job
        content.append("test-job:\n")
                .append("  stage: test\n")
                .append("  script:\n");

        if (isGradle) {
            content.append("    - ./gradlew test\n");
        } else {
            content.append("    - mvn test\n");
        }

        content.append("\n");

        // Package job
        content.append("package-job:\n")
                .append("  stage: package\n")
                .append("  script:\n");

        if (isGradle) {
            content.append("    - ./gradlew bootJar\n")
                    .append("    - mv build/libs/*.jar ").append(artifactId).append(".jar\n");
        } else {
            content.append("    - mvn clean package\n")
                    .append("    - mv target/*.jar ").append(artifactId).append(".jar\n");
        }

        try {
            Path gitlabCiPath = projectPath.resolve(".gitlab-ci.yml");
            Files.writeString(gitlabCiPath, content.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write .gitlab-ci.yml", e);
        }
    }
}