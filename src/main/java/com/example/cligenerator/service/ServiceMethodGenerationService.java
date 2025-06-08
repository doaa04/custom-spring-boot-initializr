package com.example.cligenerator.service;

import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.example.cligenerator.config.AzureConfig;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * AI service for generating additional service layer methods based on project
 * description
 */
@Service
public class ServiceMethodGenerationService extends AIGenerator {

    public ServiceMethodGenerationService(AzureConfig.AzureSettings settings) {
        super(settings);
    }

    /**
     * Generate additional service methods for a specific entity
     */
    public String generateServiceMethods(ProjectDescription description, EntityDefinition entity) {
        ProjectDescription entitySpecificDescription = new ProjectDescription(
                description.getProjectName(),
                description.getGroupId(),
                description.getArtifactId(),
                description.getPackageName(),
                description.getJavaVersion(),
                description.getSpringBootVersion(),
                description.getDependencies(),
                description.getOutputDir(),
                description.getBuildTool(),
                List.of(entity), // Focus on this specific entity
                description.getDatabaseConfig(),
                description.getGithubRemoteUrl(),
                description.isSupportingAI(),
                description.getEntitiesDescription(),
                description.isIncludesDocker(),
                description.isIncludesSwagger(),
                description.isIncludesGitlab(),
                description.isIncludesTests(),
                description.isIncludesGit(),
                description.isIncludesAIServiceMethods());

        return super.generate(entitySpecificDescription);
    }

    @Override
    public List<ChatRequestMessage> buildRequestBody(ProjectDescription description) {
        EntityDefinition entity = description.getEntities().get(0); // Focus on the first entity

        String entityFields = entity.getFields().stream()
                .filter(field -> !field.isId())
                .map(field -> field.getName() + " (" + field.getType() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("no fields");
        String prompt = String.format(
                """
                        You are an expert Spring Boot developer. Based on the project description and entity details below,
                        generate 1-2 useful additional service methods for the %s entity that would be commonly needed in a %s application.

                        Project Description: %s
                        Entity: %s
                        Entity Fields: %s

                        IMPORTANT CONSTRAINTS:
                        - You can ONLY use these existing repository methods: findAll(), findById(id), save(entity), delete(entity), deleteById(id), existsById(id), count()
                        - Do NOT create custom repository queries like findByNameContainingIgnoreCase() - they don't exist
                        - Generate ONLY the method signatures and implementations that would go inside the service implementation class
                        - Do NOT include the class declaration, imports, or constructor
                        - Use existing repository methods combined with Java streams for filtering and searching

                        Focus on business logic methods that use the available repository methods, such as:
                        - Validation methods that check business rules
                        - Utility methods that process data using streams
                        - Batch operations using existing CRUD methods
                        - Count or existence check methods

                        Examples of VALID patterns (using only existing repository methods):
                        ```java
                        @Transactional(readOnly = true)
                        public boolean existsByField(String fieldValue) {
                            return repository.findAll().stream()
                                .anyMatch(entity -> entity.getFieldName().equals(fieldValue));
                        }

                        @Transactional(readOnly = true)
                        public List<EntityDto> findByFieldContaining(String searchTerm) {
                            return repository.findAll().stream()
                                .filter(entity -> entity.getFieldName().toLowerCase().contains(searchTerm.toLowerCase()))
                                .map(this::convertToDto)
                                .collect(Collectors.toList());
                        }
                        ```

                        Return ONLY valid Java method implementations that can be directly inserted into the service class.
                        Use the same coding style and patterns as the existing CRUD methods.
                        Include proper @Transactional annotations where appropriate.
                        """,
                entity.getName(),
                description.getProjectName(),
                description.getEntitiesDescription(),
                entity.getName(),
                entityFields);

        return Arrays.asList(
                new ChatRequestSystemMessage(
                        "You are an expert Spring Boot backend developer with deep knowledge of service layer patterns."),
                new ChatRequestSystemMessage(prompt),
                new ChatRequestSystemMessage(
                        "Return only valid Java method implementations. No explanations or markdown formatting."),
                new ChatRequestSystemMessage(
                        "The methods must be wrapped between " + getStartDelimiter() + " and " + getEndDelimiter()));
    }

    @Override
    protected String getStartDelimiter() {
        return "```java";
    }

    @Override
    protected String getEndDelimiter() {
        return "```";
    }
}
