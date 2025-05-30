package com.example.cligenerator.config;

import com.example.cligenerator.service.DockerfileGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class ProjectGenerationConfig {
    @Bean
    public DockerfileGenerator dockerfileGenerator(AzureConfig.AzureSettings azureSettings) {
        return new DockerfileGenerator(azureSettings);
    }
}
