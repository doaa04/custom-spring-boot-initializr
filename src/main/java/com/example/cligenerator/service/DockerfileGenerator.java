package com.example.cligenerator.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.example.cligenerator.config.AzureConfig;

import java.util.Arrays;
import java.util.List;

public class DockerfileGenerator {
    private final ChatCompletionsClient client;
    private final String model;

    public DockerfileGenerator(AzureConfig.AzureSettings settings) {
        client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(settings.apiKey))
                .endpoint(settings.endpoint)
                .buildClient();
        model = settings.modelName;
    }

    public String generate() {
        List<ChatRequestMessage> chatMessages = buildRequestBody();
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages).setModel(model);
        ChatCompletions completions = client.complete(chatCompletionsOptions);
        System.out.printf("%s.%n", completions.getChoice().getMessage().getContent());
        String fullResponse = completions.getChoices().get(0).getMessage().getContent();
        return extractFile(fullResponse);
    }

    public List<ChatRequestMessage> buildRequestBody() {
        return Arrays.asList(
                new ChatRequestSystemMessage("You are an expert in DevOps."),
                new ChatRequestSystemMessage("Generate a Dockerfile for a Spring Boot project."),
                new ChatRequestSystemMessage("The dockerfile must be wrapped around ```dockerfile and ```.")
        );
    }

    public String extractFile(String responseBody) {
        String start = "```dockerfile";
        String end = "```";

        int startIndex = responseBody.indexOf(start);
        if (startIndex == -1) return "";

        int endIndex = responseBody.indexOf(end, startIndex + start.length());
        if (endIndex == -1) return "";

        return responseBody.substring(startIndex + start.length(), endIndex).trim();
    }
}

