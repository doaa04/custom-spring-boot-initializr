package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.AISuggestion;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DependencyCheckService extends AIGenerator {
    public DependencyCheckService(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    public AISuggestion analyze(ProjectDescription description) {
        String fullResponse = generate(description);
        String explanation = extract(fullResponse, getStartDelimiter(), getEndDelimiter());

        boolean compatible = fullResponse.toLowerCase().startsWith("yes");
        String recommendedJavaVersion = extractField(fullResponse, "Recommended Java version:");
        String recommendedSpringBootVersion = extractField(fullResponse, "Recommended Spring Boot version:");

        AISuggestion suggestion = new AISuggestion();
        suggestion.setCompatible(compatible);
        suggestion.setExplanation(explanation);
        suggestion.setRecommendedJavaVersion(recommendedJavaVersion);
        suggestion.setRecommendedSpringBootVersion(recommendedSpringBootVersion);

        return suggestion;
    }

    @Override
    public String generate(ProjectDescription description) {
        List<ChatRequestMessage> chatMessages = buildRequestBody(description);
        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages).setModel(model);
        ChatCompletions completions = client.complete(chatCompletionsOptions);
        return completions.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        StringBuilder depList = new StringBuilder();
        for (String dep : description.getDependencies()) {
            depList.append("- ").append(dep).append("\n");
        }

        String prompt = String.format("""
            I am working on a Spring Boot project using Java %s and Spring Boot %s.
            The project includes the following dependencies:
            %s
            Are all the listed dependencies compatible with the given Java and Spring Boot versions?
            
            Your response must:
            - Start with "yes" or "no" (for compatibility check).
            - Wrap the explanation between %s and %s (on separate lines).
            - If not compatible, include:
                - Recommended Java version: <only one specific version, like 17 or 21>
                - Recommended Spring Boot version: <only one specific version, like 3.2.5>
            Only give exact and supported versions, not version ranges or alternatives.
            Do not include "or" or "x" in version suggestions.
            """,
                        description.getJavaVersion(),
                        description.getSpringBootVersion(),
                        depList,
                        getStartDelimiter(),
                        getEndDelimiter()
                );

        return Arrays.asList(
                new ChatRequestSystemMessage("You are a Spring Boot and Java compatibility expert."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("The explanation must be simply presented in seperate lines"),
                new ChatRequestSystemMessage("If no dependencies found, only check the compatibility of Java and Spring Boot versions"),
                new ChatRequestSystemMessage("The explanation must be wrapped around " + getStartDelimiter() + " and " + getEndDelimiter())
        );
    }

    @Override
    protected String getStartDelimiter() {
        return "```explanation";
    }

    @Override
    protected String getEndDelimiter() {
        return "```";
    }

    private String extractField(String text, String fieldLabel) {
        int index = text.indexOf(fieldLabel);
        if (index == -1) return null;

        String line = text.substring(index + fieldLabel.length()).split("\n")[0].trim();
        return line.isEmpty() ? null : line;
    }
}
