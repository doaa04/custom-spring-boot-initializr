package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EntityGenerationService extends AIGenerator {
    public EntityGenerationService(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        String prompt = String.format("""
        You are assisting in designing the data model for a Spring Boot application named "%s".
        Based on the following project description, generate a **JSON array** of entity definitions:
        ---
        %s
        ---
        Each entity in the array must have the following structure:
        - `name`: singular name of the entity (e.g., "Task")
        - `namePlural`: plural form (e.g., "Tasks")
        - `nameLowercase`: lowercase name (e.g., "task")
        - `idField`: a single ID field:
            {
              "name": "id",
              "type": "Long",
              "isId": true
            }
        - `fields`: an array of field definitions (excluding the ID field). Each field should be:
            {
              "name": "title",
              "type": "String",
              "isId": false
            }
        - Use appropriate Java types: String, Integer, Long, Double, LocalDate, Boolean, etc.

        ⚠️ The JSON must match this structure exactly and must be valid for parsing in Java.
        """,
                description.getProjectName(),
                description.getEntitiesDescription()
        );

        return Arrays.asList(
                new ChatRequestSystemMessage("You are an expert in Java backend design and entity modeling."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("Return valid and parsable JSON only. Do not include any text outside the code block."),
                new ChatRequestSystemMessage("The json must be wrapped around " + getStartDelimiter() + " and " + getEndDelimiter())
        );
    }


    @Override
    protected String getStartDelimiter() {
        return "```json";
    }

    @Override
    protected String getEndDelimiter() {
        return "```";
    }
}
