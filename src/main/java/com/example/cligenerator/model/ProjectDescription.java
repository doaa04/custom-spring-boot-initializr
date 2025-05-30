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
    private boolean isGradle;
}