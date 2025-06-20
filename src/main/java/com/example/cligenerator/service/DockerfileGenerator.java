package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;

import java.util.Arrays;
import java.util.List;

public class DockerfileGenerator extends AIGenerator {
    public DockerfileGenerator(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        String prompt = String.format("""
            I am building a Spring Boot project named "%s" using Java %s and Spring Boot %s.
            The base package is "%s".
            Generate a Dockerfile that:
            - Builds the project using Maven
            - Runs it using `java -jar`
            - Uses a JDK base image
            """,
                description.getProjectName(),
                description.getJavaVersion(),
                description.getSpringBootVersion(),
                description.getPackageName()
        );
        return Arrays.asList(
                new ChatRequestSystemMessage("You are an expert in DevOps."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("- project name: " + description.getProjectName()),

                new ChatRequestSystemMessage("The dockerfile must be wrapped around ```dockerfile and ```.")
        );
    }

    @Override
    protected String getStartDelimiter() {
        return "```dockerfile";
    }

    @Override
    protected String getEndDelimiter() {
        return "```";
    }
}