package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DependencyCheckService extends AIGenerator {
    public DependencyCheckService(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    public boolean doMatch(ProjectDescription description) {
        String fullResponse = generate(description);
        String explanation = extract(fullResponse, getStartDelimiter(), getEndDelimiter());
        String lower = fullResponse.toLowerCase();

        System.out.println(explanation);
        return !lower.startsWith("no");
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
            Your answer must start with the word "yes" or "no".
            Explain which dependencies are likely to cause a problem.
            If not compatible, conclude your response with recommended Spring Boot and Java versions that would work best with the provided dependencies.
            """,
                description.getJavaVersion(),
                description.getSpringBootVersion(),
                depList
        );

        return Arrays.asList(
                new ChatRequestSystemMessage("You are a Spring Boot and Java compatibility expert."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage("The explanation must be simply presented in seperate lines"),
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
}
