package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.ProjectDescription;

import java.util.List;

public class TestGenerator extends AIGenerator {
    public TestGenerator(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    @Override
    public String generate(ProjectDescription description) {
        return "";
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        return List.of();
    }

    @Override
    public String extract(String responseBody) {
        return "";
    }
}
