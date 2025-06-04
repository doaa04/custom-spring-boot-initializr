package com.example.cligenerator.service;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
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

    public String generate(ProjectDescription description) {
        List<ChatRequestMessage> messages = buildRequestBody(description);
        ChatCompletionsOptions options = new ChatCompletionsOptions(messages).setModel(model);
        ChatCompletions completions = client.complete(options);
        String content = completions.getChoices().get(0).getMessage().getContent();
        return extract(content, getStartDelimiter(), getEndDelimiter());
    }

    public String extract(String responseBody, String start, String end) {
        int startIndex = responseBody.indexOf(start);
        if (startIndex == -1) return "";

        int endIndex = responseBody.indexOf(end, startIndex + start.length());
        if (endIndex == -1) return "";

        return responseBody.substring(startIndex + start.length(), endIndex).trim();
    }

    public abstract List<ChatRequestMessage> buildRequestBody(ProjectDescription description);

    protected abstract String getStartDelimiter();
    protected abstract String getEndDelimiter();
}
