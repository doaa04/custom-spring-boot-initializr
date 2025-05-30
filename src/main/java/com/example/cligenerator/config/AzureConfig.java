package com.example.cligenerator.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {

    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @Bean
    public AzureSettings azureSettings() {
        return new AzureSettings(
                dotenv.get("AZURE_API_KEY"),
                dotenv.get("AZURE_ENDPOINT"),
                dotenv.get("MODEL_NAME")
        );
    }

    public static class AzureSettings {
        public final String apiKey;
        public final String endpoint;
        public final String modelName;

        public AzureSettings(String apiKey, String endpoint, String modelName) {
            this.apiKey = apiKey;
            this.endpoint = endpoint;
            this.modelName = modelName;
        }
    }
}
