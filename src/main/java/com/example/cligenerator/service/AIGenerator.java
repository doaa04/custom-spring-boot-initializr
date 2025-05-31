package com.example.cligenerator.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;

import java.util.List;

public abstract class AIGenerator {
    protected final ChatCompletionsClient client;
    protected final String model;

    public AIGenerator(AzureConfig.AzureSettings settings) {
        client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(settings.apiKey))
                .endpoint(settings.endpoint)
                .buildClient();
        model = settings.modelName;
    }

    public abstract String generate(ProjectDescription description);
    public abstract List<ChatRequestMessage> buildRequestBody(ProjectDescription description);
    public abstract String extract(String responseBody);
}
