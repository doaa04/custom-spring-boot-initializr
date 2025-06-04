package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;
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
        String prompt = String.format("""
            Generate a professional and detailed README.md file for a Spring Boot project.
            
            Project details:
            - Name: %s
            - Java version: %s
            - Spring Boot version: %s
            - Base package: %s
            """,
                description.getProjectName(),
                description.getJavaVersion(),
                description.getSpringBootVersion(),
                description.getPackageName()
        );

        return Arrays.asList(
                new ChatRequestSystemMessage("You are an expert software architect and technical writer."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("The readme file must be wrapped around " + getStartDelimiter() + " and " + getEndDelimiter())
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