package com.example.cligenerator;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.*;
import com.example.cligenerator.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class CliRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CliRunner.class);
    private final InitializrService initializrService;
    private final CodeGeneratorService codeGeneratorService;
    private final TestGenerationService testGenerationService;
    private final DockerService dockerService;
    private final GitlabService gitlabService;
    private final GitService gitService;
    private final DependencyCheckService dependencyCheckService;
    private final EntityGenerationService entityGenerationService;
    private final TemplateService templateService;
    private final ApiDocumentationService apiDocumentationService;
    private final ConfigurationService configurationService;
    private final ProjectGenerationService projectGenerationService;
    private final Scanner scanner = new Scanner(System.in);

    public CliRunner(InitializrService initializrService,
            CodeGeneratorService codeGeneratorService,
            TestGenerationService testGenerationService,
            DockerService dockerService,
            GitlabService gitlabService,
            GitService gitService,
            DependencyCheckService dependencyCheckService,
            EntityGenerationService entityGenerationService,
            TemplateService templateService,
            ApiDocumentationService apiDocumentationService,
            ConfigurationService configurationService,
            ProjectGenerationService projectGenerationService) {
        this.initializrService = initializrService;
        this.codeGeneratorService = codeGeneratorService;
        this.testGenerationService = testGenerationService;
        this.dockerService = dockerService;
        this.gitlabService = gitlabService;
        this.gitService = gitService;
        this.dependencyCheckService = dependencyCheckService;
        this.entityGenerationService = entityGenerationService;
        this.templateService = templateService;
        this.apiDocumentationService = apiDocumentationService;
        this.configurationService = configurationService;
        this.projectGenerationService = projectGenerationService;
    }

    @Override
    public void run(String... args) {
        ConfigurationService.ProjectConfiguration config = null;

        try {
            logger.info("Starting Enhanced Spring Boot API Generator");
            System.out.println("=== Enhanced Spring Boot API Generator ===");

            // Load configuration
            Path configPath = configurationService.getDefaultConfigPath();
            config = configurationService.loadConfiguration(configPath);

            if (!configurationService.validateConfiguration(config)) {
                logger.warn("Configuration validation failed, using defaults");
                config = new ConfigurationService.ProjectConfiguration();
            }

            // Check for custom templates
            System.out.print("Use custom templates? Enter directory path (or press Enter to use defaults): ");
            String customTemplateDir = scanner.nextLine().trim();
            if (!customTemplateDir.isEmpty()) {
                Path templateDir = Path.of(customTemplateDir);
                if (Files.exists(templateDir)) {
                    templateService.loadCustomTemplates(templateDir);
                    System.out.println("✓ Loaded custom templates from: " + templateDir);
                } else {
                    System.out.println("⚠ Custom template directory not found, using defaults");
                }
            } // --- Enhanced Project Details with Validation ---
            System.out.println("\n--- Project Configuration ---");

            String projectName = getValidatedInput("Enter project name (e.g., my-api): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9-_]+"),
                    "Project name must contain only letters, numbers, hyphens, and underscores");

            String groupId = getValidatedInput("Enter Group ID (e.g., com.example): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9._-]+"),
                    "Group ID must be a valid package name format");

            String artifactId = getValidatedInput("Enter Artifact ID (e.g., demo): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9-_]+"),
                    "Artifact ID must contain only letters, numbers, hyphens, and underscores");

            String defaultPackage = groupId + "." + artifactId.replace("-", "");
            System.out.print("Enter base package name (default: " + defaultPackage + "): ");
            String packageName = scanner.nextLine().trim();
            if (packageName.isEmpty()) {
                packageName = defaultPackage;
            } else if (!packageName.matches("[a-zA-Z0-9._]+")) {
                throw new GenerationException("Input Validation", "Invalid package name format",
                        "Package name must be a valid Java package name");
            }

            String javaVersion = getInputWithDefault("Enter Java version (default: " +
                    config.getDefaultValues().get("javaVersion") + "): ",
                    config.getDefaultValues().get("javaVersion"));

            String springBootVersion = getInputWithDefault("Enter Spring Boot version (default: " +
                    config.getDefaultValues().get("springBootVersion") + "): ",
                    config.getDefaultValues().get("springBootVersion"));

            String dependencies = "web,data-jpa,lombok,validation";

            // Enhanced Database Selection
            System.out.println("\n--- Database Configuration ---");
            System.out.println("Choose a database:");
            DatabaseConfig.DatabaseType[] dbTypes = DatabaseConfig.DatabaseType.values();
            for (int i = 0; i < dbTypes.length; i++) {
                System.out.println((i + 1) + ". " + dbTypes[i].getDisplayName());
            }
            System.out.print("Enter your choice (1-" + dbTypes.length + ", default: 1 for H2): ");
            String dbChoice = scanner.nextLine().trim();

            DatabaseConfig.DatabaseType selectedDbType = DatabaseConfig.DatabaseType.H2; // default
            if (!dbChoice.isEmpty()) {
                try {
                    int choice = Integer.parseInt(dbChoice);
                    if (choice >= 1 && choice <= dbTypes.length) {
                        selectedDbType = dbTypes[choice - 1];
                    } else {
                        System.out.println("Invalid choice. Using H2 as default.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Using H2 as default.");
                }
            }

            System.out.println("Selected database: " + selectedDbType.getDisplayName());
            dependencies += "," + selectedDbType.getSpringInitializrName();

            // Create database configuration
            DatabaseConfig databaseConfig = DatabaseConfig.createDefaultConfig(selectedDbType, projectName);

            if (selectedDbType != DatabaseConfig.DatabaseType.H2) {
                System.out.println("\nWarning: You selected " + selectedDbType.getDisplayName() + ".");
                System.out.println("Make sure you have the database server running and accessible.");
                System.out.println("The generated application.properties will contain placeholder connection details.");
            } // Add OpenAPI support
            System.out.print("Include OpenAPI/Swagger documentation? (yes/no, default: yes): ");
            String includeOpenApi = scanner.nextLine().trim();
            boolean generateOpenApi = includeOpenApi.isEmpty() || "yes".equalsIgnoreCase(includeOpenApi);

            // Note: We'll add OpenAPI dependency manually to pom.xml after project
            // generation
            // since Spring Initializr doesn't support the springdoc dependency directly

            // fetching dependencies
            String metadataUrl = "https://start.spring.io/metadata/client";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(metadataUrl, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode dependenciesNode = root.path("dependencies").path("values");

            System.out.println("\n--- Available dependency categories ---");
            Map<Integer, JsonNode> categoryMap = new HashMap<>();
            int index = 1;
            for (JsonNode category : dependenciesNode) {
                categoryMap.put(index, category);
                System.out.println(index + ". " + category.path("name").asText());
                index++;
            }
            System.out.print("Select categories by number (comma-separated): ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                for (String part : input.split(",")) {
                    try {
                        int selectedIndex = Integer.parseInt(part.trim());
                        JsonNode category = categoryMap.get(selectedIndex);
                        if (category == null)
                            continue;

                        System.out.println("\nDependencies in " + category.path("name").asText() + ":");
                        for (JsonNode dep : category.path("values")) {
                            String id = dep.path("id").asText();
                            String name = dep.path("name").asText();
                            System.out.println("- " + name + " (" + id + ")");
                        }

                        System.out.print(
                                "Enter dependencies to add (comma-separated. Already chosen: " + dependencies + "): ");
                        String depsInput = scanner.nextLine().trim();
                        if (!depsInput.isEmpty()) {
                            for (String dep : depsInput.split(",")) {
                                String depId = dep.trim();
                                if (!dependencies.contains(depId)) {
                                    dependencies += "," + depId;
                                }
                            }
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input: " + part.trim());
                    }
                }
            }

            System.out.print("Choose build tool (maven/gradle, default: maven): ");
            String buildTool = scanner.nextLine().trim().toLowerCase();
            if (!buildTool.equals("gradle") && !buildTool.equals("maven")) {
                buildTool = "maven"; // default
            }

            System.out.print("Enter output directory for the generated project (e.g., ./generated-projects): ");
            String outputDir = scanner.nextLine().trim();
            if (outputDir.isEmpty())
                outputDir = "./generated-projects";

            // --- Additional Features ---
            System.out.print("Generate Docker configuration? (yes/no, default: yes): ");
            String generateDocker = scanner.nextLine().trim();
            boolean includeDocker = generateDocker.isEmpty() || "yes".equalsIgnoreCase(generateDocker);

            System.out.print("Generate Gitlab configuration? (yes/no, default: yes): ");
            String generateGitlab = scanner.nextLine().trim();
            boolean includeGitlab = generateGitlab.isEmpty() || "yes".equalsIgnoreCase(generateGitlab);

            System.out.print("Generate comprehensive tests? (yes/no, default: yes): ");
            String generateTests = scanner.nextLine().trim();
            boolean includeTests = generateTests.isEmpty() || "yes".equalsIgnoreCase(generateTests);

            System.out.print("Push this project to a remote Git repository? (yes/no, default: yes): ");
            String setUpGit = scanner.nextLine().trim();
            boolean pushToGit = setUpGit.isEmpty() || "yes".equalsIgnoreCase(setUpGit);
            String remoteUrl = "";
            if (pushToGit) {
                while (true) {
                    System.out.print(
                            "Enter your remote Git repository URL (e.g., https://token@github.com/user/my-repo.git): ");
                    remoteUrl = scanner.nextLine().trim();

                    if (remoteUrl.isEmpty()) {
                        System.out.println("Remote URL cannot be empty. Please try again.");
                        continue;
                    }

                    // Basic validation
                    boolean isValidHttps = remoteUrl.startsWith("https://") && remoteUrl.endsWith(".git");
                    boolean isValidSsh = remoteUrl.matches("^git@[^\\s:]+:[^\\s]+\\.git$");

                    if (isValidHttps || isValidSsh)
                        break;
                    else
                        System.out.println(
                                "Invalid Git URL. Make sure it starts with 'https://' and ends with '.git', or follows the SSH format.");
                }
            }

            // --- Entity Definitions ---
            System.out.print(
                    "Would you like to use AI to generate your project from a description? (yes to use AI / no to enter entities manually, default: yes): ");
            String useAI = scanner.nextLine().trim();
            boolean enableAI = useAI.isEmpty() || "yes".equalsIgnoreCase(useAI);
            List<EntityDefinition> entityDefinitions = new ArrayList<>();
            String entitiesDescription = ""; // creating a project description instance to be passed later into
                                             // concerned services
            ProjectDescription description = new ProjectDescription(
                    projectName, groupId, artifactId, packageName, javaVersion, springBootVersion,
                    Arrays.asList(dependencies.split(",")), outputDir, buildTool, entityDefinitions, databaseConfig,
                    remoteUrl, enableAI, entitiesDescription, includeDocker, generateOpenApi, includeGitlab,
                    includeTests, pushToGit, false);

            if (enableAI) {
                System.out.print(
                        "Describe your project (e.g. 'A school management system with students and teachers'): ");
                entitiesDescription = scanner.nextLine().trim();
                description.setEntitiesDescription(entitiesDescription);
                String entitiesJson = entityGenerationService.generate(description);
                // System.out.println(entitiesJson);
                entityDefinitions = mapper.readValue(entitiesJson, new TypeReference<List<EntityDefinition>>() {
                });
            } else {
                boolean addMoreEntities = true;
                while (addMoreEntities) {
                    System.out.println("\n--- Define Entity ---");
                    System.out.print("Enter entity name (e.g., Product, User - singular, capitalized): ");
                    String entityNameInput = scanner.nextLine().trim();
                    if (entityNameInput.isEmpty()) {
                        System.out.println("Entity name cannot be empty. Skipping this entity.");
                        continue;
                    }
                    EntityDefinition currentEntity = new EntityDefinition(entityNameInput);

                    // Add default ID field
                    currentEntity.addField(new FieldDefinition("id", "Long", true));

                    boolean addMoreFields = true;
                    while (addMoreFields) {
                        System.out.print(
                                "Enter field name (or type 'done' if no more fields for " + currentEntity.getName()
                                        + "): ");
                        String fieldName = scanner.nextLine().trim();
                        if ("done".equalsIgnoreCase(fieldName)) {
                            addMoreFields = false;
                            continue;
                        }
                        if (fieldName.isEmpty() || fieldName.equalsIgnoreCase("id")) {
                            System.out.println("Invalid field name or 'id' (already added). Try again.");
                            continue;
                        }

                        System.out.print(
                                "Enter field type (String, Integer, Long, Double, Boolean, LocalDate, BigDecimal - default: String): ");
                        String fieldType = scanner.nextLine().trim();
                        if (fieldType.isEmpty())
                            fieldType = "String";
                        // Basic validation for common types
                        List<String> validTypes = Arrays.asList("String", "Integer", "Long", "Double", "Boolean",
                                "LocalDate",
                                "BigDecimal");
                        if (!validTypes.contains(fieldType)) {
                            System.out.println(
                                    "Warning: Field type '" + fieldType
                                            + "' might require additional imports/configuration.");
                        }

                        currentEntity.addField(new FieldDefinition(fieldName, fieldType, false));
                    }
                    entityDefinitions.add(currentEntity);

                    System.out.print("Add another entity? (yes/no, default: no): ");
                    String addAnotherEntityResponse = scanner.nextLine().trim();
                    addMoreEntities = "yes".equalsIgnoreCase(addAnotherEntityResponse);
                }
                if (entityDefinitions.isEmpty()) {
                    throw new GenerationException("Entity Configuration", "No entities defined",
                            "At least one entity is required for code generation");
                }
            }

            description.setEntities(entityDefinitions);

            // dependency ai check
            System.out.println("\n--- Dependency analysis ---");
            AISuggestion suggestion = dependencyCheckService.analyze(description);
            System.out.println(suggestion.getExplanation());
            boolean areCompatible = suggestion.isCompatible();
            if (!areCompatible) {
                System.out.println("\nSuggested Configuration:");
                String recommendedJavaVersion = suggestion.getRecommendedJavaVersion();
                String recommendedSpringBootVersion = suggestion.getRecommendedSpringBootVersion();
                System.out.println("- Java version: " + recommendedJavaVersion);
                System.out.println("- Spring Boot version: " + recommendedSpringBootVersion);
                System.out.print("\nWould you like to proceed with suggested configuration? (yes/no, default: no): ");
                String acceptSuggestion = scanner.nextLine().trim();
                boolean useSuggestion = "yes".equalsIgnoreCase(acceptSuggestion);
                if (useSuggestion) {
                    description.setJavaVersion(recommendedJavaVersion);
                    description.setSpringBootVersion(recommendedSpringBootVersion);
                }
            } // --- AI Service Method Generation ---
            System.out.println("\n--- Enhanced Service Layer Generation ---");
            System.out.print(
                    "Generate AI-powered additional service methods based on your project description? (yes/no, default: yes): ");
            String generateAIServiceMethods = scanner.nextLine().trim();
            boolean includeAIServiceMethods = generateAIServiceMethods.isEmpty()
                    || "yes".equalsIgnoreCase(generateAIServiceMethods);
            if (includeAIServiceMethods) {
                System.out.println("✓ Will generate 1-2 additional useful service methods per entity using AI");
                System.out.println("  These methods will be based on your project description and entity structure");
            }

            // Set the AI service methods preference in the description
            description.setIncludesAIServiceMethods(includeAIServiceMethods);

            System.out.println("\nStarting project generation...");
            logger.info("Generating project: {}", projectName);

            Path projectBasePath = projectGenerationService.generateProject(description);

            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ SUCCESS: Enhanced project '" + projectName + "' generated at: "
                    + projectBasePath.toAbsolutePath());
            System.out.println("\n📦 Generated components:");
            System.out.println("✓ Spring Boot application structure with best practices");
            System.out.println("✓ Entity classes with JPA annotations and DTOs");
            System.out.println("✓ Repository, Service, and Controller layers with pagination");
            System.out.println("✓ Database configuration with connection pooling");

            if (generateOpenApi) {
                System.out.println("✓ Comprehensive OpenAPI/Swagger documentation with:");
                System.out.println("  • Enhanced API information and contact details");
                System.out.println("  • Global exception handling with structured error responses");
                System.out.println("  • Resource-specific tags and operation descriptions");
                System.out.println("  📊 API documentation: http://localhost:8080/swagger-ui.html");
            }

            if (includeDocker) {
                System.out.println("✓ Production-ready Docker configuration:");
                System.out.println("  • Multi-stage Dockerfile with optimization");
                System.out.println("  • Docker Compose with database service");
                System.out.println("  • Environment-specific configurations");
            }

            if (includeTests) {
                System.out.println("✓ Comprehensive test suite:");
                System.out.println("  • Unit tests for service layer with Mockito");
                System.out.println("  • Integration tests for controllers with MockMvc");
                System.out.println("  • Repository tests with @DataJpaTest");
                System.out.println("  • Test data generators for all field types");
            }

            System.out.println("\n🚀 Quick Start:");
            System.out.println("1. cd " + projectBasePath.toAbsolutePath());
            System.out.println("2. ./mvnw spring-boot:run");

            if (includeDocker) {
                System.out.println("\n🐳 Docker Quick Start:");
                System.out.println("1. ./mvnw clean package");
                System.out.println("2. docker-compose up -d");
            }

            System.out.println("\n📝 Next Steps:");
            System.out.println("• Update application.properties with your database configuration");
            System.out.println("• Customize validation annotations in entity classes");
            System.out.println("• Add business logic to service implementations");
            System.out.println("• Configure security if needed");
            System.out.println("=".repeat(80));

        } catch (GenerationException e) {
            logger.error("Generation failed in {}: {}", e.getComponent(), e.getMessage(), e);
            System.err.println("❌ Generation failed in " + e.getComponent() + ": " + e.getMessage());
            System.err.println("Details: " + e.getDetails());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error during generation", e);
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            scanner.close();
        }
    }

    /**
     * Get validated input with custom validation function
     */
    private String getValidatedInput(String prompt, java.util.function.Predicate<String> validator,
            String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println("❌ " + errorMessage + " Please try again.");
        }
    }

    /**
     * Get input with default value
     */
    private String getInputWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Get boolean input with default
     */
}