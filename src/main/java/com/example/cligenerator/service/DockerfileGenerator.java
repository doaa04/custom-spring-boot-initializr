package com.example.cligenerator.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;

import java.util.Arrays;
import java.util.List;

public class DockerfileGenerator extends AIGenerator {
    public DockerfileGenerator(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    @Override
    public String generate(ProjectDescription description) {
        List<ChatRequestMessage> chatMessages = buildRequestBody(description);
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages).setModel(model);
        ChatCompletions completions = client.complete(chatCompletionsOptions);
        System.out.printf("%s.%n", completions.getChoice().getMessage().getContent());
        String fullResponse = completions.getChoices().get(0).getMessage().getContent();
        return extract(fullResponse);
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
    public String extract(String responseBody) {
        String start = "```dockerfile";
        String end = "```";

        int startIndex = responseBody.indexOf(start);
        if (startIndex == -1) return "";

        int endIndex = responseBody.indexOf(end, startIndex + start.length());
        if (endIndex == -1) return "";

        return responseBody.substring(startIndex + start.length(), endIndex).trim();
    }
}