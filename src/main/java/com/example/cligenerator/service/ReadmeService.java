package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.DatabaseConfig;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ReadmeService extends AIGenerator {
    public ReadmeService(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        StringBuilder depList = new StringBuilder();
        for (String dep : description.getDependencies()) {
            depList.append("- ").append(dep).append("\n");
        }

        StringBuilder entityList = new StringBuilder();
        if (description.getEntities() != null && !description.getEntities().isEmpty()) {
            for (EntityDefinition entity : description.getEntities()) {
                entityList.append("- ").append(entity.getName()).append("\n");
            }
        } else {
            entityList.append("No entities defined.\n");
        }

        if (description.isSupportingAI()) entityList.append(" - project description: ").append(description.getEntitiesDescription());

        DatabaseConfig dbConfig = description.getDatabaseConfig();
        String dbInfo = String.format("""
            Database Configuration:
            - Type: %s
            - URL: %s
            - Username: %s
            - Driver: %s
            """,
                dbConfig.getType().getDisplayName(),
                dbConfig.getUrl(),
                dbConfig.getUsername(),
                dbConfig.getDriverClassName()
        );

        String prompt = getString(description, depList, entityList, dbInfo);

        return Arrays.asList(
                new ChatRequestSystemMessage("You are an expert software architect and technical writer."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("The readme file must be wrapped around " + getStartDelimiter() + " and " + getEndDelimiter())
        );
    }

    private static @NotNull String getString(ProjectDescription description, StringBuilder depList, StringBuilder entityList, String dbInfo) {
        return String.format("""
            Generate a professional and detailed README.md file for a Spring Boot project.
            
            Project details:
            - Name: %s
            - Java version: %s
            - Spring Boot version: %s
            - Base package: %s
            - Dependencies:
            %s
            - Entities:
            %s
            %s
            """,
                    description.getProjectName(),
                    description.getJavaVersion(),
                    description.getSpringBootVersion(),
                    description.getPackageName(),
                depList,
                entityList,
                    dbInfo
            );
    }

    @Override
    protected String getStartDelimiter() {
        return "```readme";
    }

    @Override
    protected String getEndDelimiter() {
        return "```";
    }
}