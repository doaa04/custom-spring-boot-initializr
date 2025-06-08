package com.example.cligenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDescription {
    private String projectName;
    private String groupId;
    private String artifactId;
    private String packageName;
    private String javaVersion;
    private String springBootVersion;
    private List<String> dependencies;
    private String outputDir;
    private String buildTool;
    private List<EntityDefinition> entities;
    private DatabaseConfig databaseConfig;
    private String githubRemoteUrl;
    private boolean isSupportingAI;
    private String entitiesDescription;
    private boolean includesDocker;
    private boolean includesSwagger;
    private boolean includesGitlab;
    private boolean includesTests;
    private boolean includesGit;
    private boolean includesAIServiceMethods;
}