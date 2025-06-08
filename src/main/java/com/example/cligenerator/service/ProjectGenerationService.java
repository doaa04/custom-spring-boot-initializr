package com.example.cligenerator.service;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.DatabaseConfig;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ProjectGenerationService {
    private final InitializrService initializrService;
    private final CodeGeneratorService codeGeneratorService;
    private final ApiDocumentationService apiDocumentationService;
    private final DockerService dockerService;
    private final GitlabService gitlabService;
    private final TestGenerationService testGenerationService;
    private final GitService gitService;
    private final ServiceMethodGenerationService serviceMethodGenerationService;

    public ProjectGenerationService(InitializrService initializrService, CodeGeneratorService codeGeneratorService,
            ApiDocumentationService apiDocumentationService, DockerService dockerService, GitlabService gitlabService,
            TestGenerationService testGenerationService, GitService gitService,
            ServiceMethodGenerationService serviceMethodGenerationService) {
        this.initializrService = initializrService;
        this.codeGeneratorService = codeGeneratorService;
        this.apiDocumentationService = apiDocumentationService;
        this.dockerService = dockerService;
        this.gitlabService = gitlabService;
        this.testGenerationService = testGenerationService;
        this.gitService = gitService;
        this.serviceMethodGenerationService = serviceMethodGenerationService;
    }

    public Path generateProject(ProjectDescription description) throws GenerationException {
        String projectName = description.getProjectName();
        String packageName = description.getPackageName();
        String artifactId = description.getArtifactId();
        String javaVersion = description.getJavaVersion();
        DatabaseConfig databaseConfig = description.getDatabaseConfig();
        List<EntityDefinition> entityDefinitions = description.getEntities();
        boolean generateOpenApi = description.isIncludesSwagger();
        boolean includeGitlab = description.isIncludesGitlab();
        boolean includeDocker = description.isIncludesDocker();
        boolean includeTests = description.isIncludesTests();
        boolean pushToGit = description.isIncludesGit();
        String depList = String.join(",", description.getDependencies());

        Path projectBasePath;
        try {
            projectBasePath = initializrService.downloadAndUnzipProject(
                    projectName, description.getGroupId(), artifactId, packageName,
                    description.getJavaVersion(), description.getSpringBootVersion(), depList,
                    description.getBuildTool(), description.getOutputDir());
        } catch (Exception e) {
            throw new GenerationException("Spring Initializr", "Failed to generate base project",
                    "Error downloading from Spring Initializr: " + e.getMessage(), e);
        }

        // Find the actual package where the main class was created
        String actualPackageName = findMainClassPackage(projectBasePath, artifactId);
        if (actualPackageName != null) {
            packageName = actualPackageName;
            System.out.println("Using detected package: " + packageName);
        }

        try {
            // Generate application.properties with database configuration
            codeGeneratorService.generateApplicationProperties(projectBasePath, databaseConfig);

            // Generate entity code
            for (EntityDefinition entityDef : entityDefinitions) {
                // logger.info("Generating code for entity: {}", entityDef.getName());
                codeGeneratorService.generateCode(projectBasePath, packageName, entityDef);
            } // Generate comprehensive API documentation if requested
            if (generateOpenApi) {
                System.out.println("Adding OpenAPI dependency...");
                codeGeneratorService.addOpenApiDependency(projectBasePath, description);
                System.out.println("Generating comprehensive API documentation...");
                apiDocumentationService.generateApiDocumentation(projectBasePath, packageName, projectName,
                        entityDefinitions);
            }

            // Generate Docker configuration if requested
            if (includeDocker) {
                System.out.println("Generating Docker configuration...");
                dockerService.generateDockerConfiguration(projectBasePath, artifactId, javaVersion, databaseConfig);
            }

            // Generate Gitlab configuration if requested
            if (includeGitlab) {
                System.out.println("Generating Gitlab configuration...");
                gitlabService.generateGitlabConfiguration(projectBasePath, description);
            } // Generate tests if requested
            if (includeTests) {
                System.out.println("Generating comprehensive tests...");
                for (EntityDefinition entityDef : entityDefinitions) {
                    testGenerationService.generateTestsForEntity(projectBasePath, packageName, entityDef);
                }
            }

            // Generate AI-powered service methods if requested
            if (description.isIncludesAIServiceMethods()) {
                System.out.println("Generating AI-powered service methods...");
                for (EntityDefinition entityDef : entityDefinitions) {
                    try {
                        String aiMethods = serviceMethodGenerationService.generateServiceMethods(description,
                                entityDef);
                        if (aiMethods != null && !aiMethods.trim().isEmpty()) {
                            addAIMethodsToServiceImplementation(projectBasePath, packageName, entityDef, aiMethods);
                            addAIMethodSignaturesToServiceInterface(projectBasePath, packageName, entityDef, aiMethods);
                            addAIEndpointsToController(projectBasePath, packageName, entityDef, aiMethods);
                            System.out
                                    .println("✓ Added AI-generated methods to " + entityDef.getName()
                                            + "Service, ServiceImpl, and Controller");
                        }
                    } catch (Exception e) {
                        System.out.println("⚠ Warning: Could not generate AI methods for " + entityDef.getName() + ": "
                                + e.getMessage());
                    }
                }
            }

            if (pushToGit) {
                System.out.println("Setting up GitHub repository...");
                gitService.initPush(projectBasePath, description);
            }
        } catch (Exception e) {
            throw new GenerationException("Code Generation", "Failed to generate project files",
                    "Error during code generation: " + e.getMessage(), e);
        }
        return projectBasePath;
    }

    private String findMainClassPackage(Path projectBasePath, String artifactId) {
        try {
            String mainClassName = toCamelCase(artifactId) + "Application";
            Path srcPath = projectBasePath.resolve("src/main/java");

            return Files.walk(srcPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(mainClassName + ".java"))
                    .findFirst()
                    .map(path -> {
                        Path relativePath = srcPath.relativize(path.getParent());
                        return relativePath.toString().replace('/', '.').replace('\\', '.');
                    })
                    .orElse(null);
        } catch (Exception e) {
            System.out.println("Could not detect main class package, using provided package name");
            return null;
        }
    }

    private String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Add AI-generated methods to the service implementation class
     */
    private void addAIMethodsToServiceImplementation(Path projectBasePath, String packageName,
            EntityDefinition entityDef, String aiMethods) throws Exception {
        try {
            String entityName = entityDef.getName();
            String serviceImplFileName = entityName + "ServiceImpl.java";

            // Find the service implementation file
            Path serviceImplPath = projectBasePath
                    .resolve("src/main/java")
                    .resolve(packageName.replace('.', '/'))
                    .resolve("service")
                    .resolve("impl")
                    .resolve(serviceImplFileName);

            if (!Files.exists(serviceImplPath)) {
                System.out.println("⚠ Warning: Service implementation file not found: " + serviceImplPath);
                return;
            }

            // Read the current file content
            String currentContent = Files.readString(serviceImplPath);

            // Find the position before the last closing brace
            int lastBraceIndex = currentContent.lastIndexOf('}');
            if (lastBraceIndex == -1) {
                System.out.println("⚠ Warning: Could not find closing brace in service implementation");
                return;
            }

            // Clean up the AI methods (remove any markdown formatting if present)
            String cleanMethods = aiMethods.trim();
            if (cleanMethods.startsWith("```java")) {
                cleanMethods = cleanMethods.substring(7);
            }
            if (cleanMethods.endsWith("```")) {
                cleanMethods = cleanMethods.substring(0, cleanMethods.length() - 3);
            }
            cleanMethods = cleanMethods.trim();

            // Insert the AI-generated methods before the last closing brace
            StringBuilder updatedContent = new StringBuilder();
            updatedContent.append(currentContent.substring(0, lastBraceIndex));
            updatedContent.append("\n    // AI-Generated Service Methods\n");
            updatedContent.append("    ").append(cleanMethods.replace("\n", "\n    "));
            updatedContent.append("\n");
            updatedContent.append(currentContent.substring(lastBraceIndex));

            // Write the updated content back to the file
            Files.writeString(serviceImplPath, updatedContent.toString());

        } catch (Exception e) {
            throw new GenerationException("AI Service Methods",
                    "Failed to add AI methods to " + entityDef.getName() + "ServiceImpl",
                    "Error modifying service implementation: " + e.getMessage(), e);
        }
    }

    /**
     * Add AI-generated method signatures to the service interface
     */
    private void addAIMethodSignaturesToServiceInterface(Path projectBasePath, String packageName,
            EntityDefinition entityDef, String aiMethods) throws Exception {
        try {
            String entityName = entityDef.getName();
            String serviceFileName = entityName + "Service.java";

            // Find the service interface file
            Path servicePath = projectBasePath
                    .resolve("src/main/java")
                    .resolve(packageName.replace('.', '/'))
                    .resolve("service")
                    .resolve(serviceFileName);

            if (!Files.exists(servicePath)) {
                System.out.println("⚠ Warning: Service interface file not found: " + servicePath);
                return;
            }

            // Read the current file content
            String currentContent = Files.readString(servicePath);

            // Extract method signatures from the AI-generated implementation
            String methodSignatures = extractMethodSignatures(aiMethods);

            if (methodSignatures.trim().isEmpty()) {
                return;
            }

            // Find the position before the last closing brace
            int lastBraceIndex = currentContent.lastIndexOf('}');
            if (lastBraceIndex == -1) {
                System.out.println("⚠ Warning: Could not find closing brace in service interface");
                return;
            }

            // Insert the method signatures before the last closing brace
            StringBuilder updatedContent = new StringBuilder();
            updatedContent.append(currentContent.substring(0, lastBraceIndex));
            updatedContent.append("\n    // AI-Generated Service Method Signatures\n");
            updatedContent.append("    ").append(methodSignatures.replace("\n", "\n    "));
            updatedContent.append("\n");
            updatedContent.append(currentContent.substring(lastBraceIndex));

            // Write the updated content back to the file
            Files.writeString(servicePath, updatedContent.toString());

        } catch (Exception e) {
            throw new GenerationException("AI Service Interface",
                    "Failed to add AI method signatures to " + entityDef.getName() + "Service",
                    "Error modifying service interface: " + e.getMessage(), e);
        }
    }

    /**
     * Extract method signatures from AI-generated method implementations
     */
    private String extractMethodSignatures(String aiMethods) {
        StringBuilder signatures = new StringBuilder();

        // Clean up the AI methods
        String cleanMethods = aiMethods.trim();
        if (cleanMethods.startsWith("```java")) {
            cleanMethods = cleanMethods.substring(7);
        }
        if (cleanMethods.endsWith("```")) {
            cleanMethods = cleanMethods.substring(0, cleanMethods.length() - 3);
        }

        // Split by lines and extract method signatures
        String[] lines = cleanMethods.split("\n");
        boolean inMethodSignature = false;
        StringBuilder currentSignature = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Skip annotations and comments
            if (trimmedLine.startsWith("@") || trimmedLine.startsWith("//") || trimmedLine.startsWith("/*")) {
                continue;
            }

            // Detect method start (public, private, protected keywords and method signature
            // pattern)
            if (trimmedLine.matches(".*\\b(public|private|protected)\\b.*\\(.*") && trimmedLine.contains(")")) {
                // This looks like a complete method signature on one line
                String signature = extractSignatureFromLine(trimmedLine);
                if (!signature.isEmpty()) {
                    signatures.append(signature).append(";\n");
                }
            } else if (trimmedLine.matches(".*\\b(public|private|protected)\\b.*")) {
                // This might be a multi-line method signature
                inMethodSignature = true;
                currentSignature = new StringBuilder(trimmedLine);
            } else if (inMethodSignature) {
                currentSignature.append(" ").append(trimmedLine);
                if (trimmedLine.contains(")")) {
                    // End of method signature
                    String signature = extractSignatureFromLine(currentSignature.toString());
                    if (!signature.isEmpty()) {
                        signatures.append(signature).append(";\n");
                    }
                    inMethodSignature = false;
                    currentSignature = new StringBuilder();
                }
            }
        }

        return signatures.toString();
    }

    /**
     * Extract clean method signature from a line containing method declaration
     */
    private String extractSignatureFromLine(String line) {
        // Remove method body and extract just the signature
        String trimmed = line.trim();
        int braceIndex = trimmed.indexOf('{');
        if (braceIndex > 0) {
            trimmed = trimmed.substring(0, braceIndex).trim();
        }

        // Ensure it has proper method signature format
        if (trimmed.contains("(") && trimmed.contains(")") &&
                (trimmed.contains("public") || trimmed.contains("private") || trimmed.contains("protected"))) {
            return trimmed;
        }

        return "";
    }

    /**
     * Generate controller endpoints based on AI service methods
     */
    private String generateControllerEndpoints(EntityDefinition entityDef, String aiMethods) {
        StringBuilder endpoints = new StringBuilder();

        // Clean up the AI methods
        String cleanMethods = aiMethods.trim();
        if (cleanMethods.startsWith("```java")) {
            cleanMethods = cleanMethods.substring(7);
        }
        if (cleanMethods.endsWith("```")) {
            cleanMethods = cleanMethods.substring(0, cleanMethods.length() - 3);
        }

        // Extract method signatures and generate corresponding endpoints
        String[] lines = cleanMethods.split("\n");
        String entityName = entityDef.getName();
        String entityNameLower = entityDef.getNameLowercase();

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Look for public method signatures
            if (trimmedLine.matches(".*\\bpublic\\b.*\\(.*\\).*\\{.*") ||
                    (trimmedLine.matches(".*\\bpublic\\b.*\\(.*") && !trimmedLine.contains("{"))) {

                String methodSignature = extractMethodSignatureOnly(trimmedLine);
                if (!methodSignature.isEmpty()) {
                    String endpoint = generateEndpointFromMethod(entityName, entityNameLower, methodSignature);
                    if (!endpoint.isEmpty()) {
                        endpoints.append(endpoint).append("\n\n");
                    }
                }
            }
        }

        return endpoints.toString();
    }

    /**
     * Extract just the method signature part from a line
     */
    private String extractMethodSignatureOnly(String line) {
        String trimmed = line.trim();
        int braceIndex = trimmed.indexOf('{');
        if (braceIndex > 0) {
            trimmed = trimmed.substring(0, braceIndex).trim();
        }
        return trimmed;
    }

    /**
     * Generate REST endpoint based on a service method signature
     */
    private String generateEndpointFromMethod(String entityName, String entityNameLower, String methodSignature) {
        // Parse method signature to extract method name, return type, and parameters
        String methodName = extractMethodName(methodSignature);
        String returnType = extractReturnType(methodSignature);
        String parameters = extractMethodParameters(methodSignature);

        if (methodName.isEmpty()) {
            return "";
        }

        StringBuilder endpoint = new StringBuilder();

        // Determine HTTP method and path based on method name patterns
        if (methodName.startsWith("find") || methodName.startsWith("search") || methodName.startsWith("get")) {
            // GET endpoint for search/find methods
            String pathParam = generatePathFromMethodName(methodName, entityNameLower);
            endpoint.append("@GetMapping(\"").append(pathParam).append("\")\n");
            endpoint.append("public ResponseEntity<").append(mapReturnTypeForController(returnType)).append("> ");
            endpoint.append(methodName).append("(");
            endpoint.append(generateControllerParameters(parameters));
            endpoint.append(") {\n");
            endpoint.append("    ").append(generateControllerMethodBody(methodName, entityNameLower, returnType));
            endpoint.append("\n}");

        } else if (methodName.startsWith("exists") || methodName.startsWith("validate")) {
            // GET endpoint for validation/existence checks
            String pathParam = generatePathFromMethodName(methodName, entityNameLower);
            endpoint.append("@GetMapping(\"").append(pathParam).append("\")\n");
            endpoint.append("public ResponseEntity<Boolean> ");
            endpoint.append(methodName).append("(");
            endpoint.append(generateControllerParameters(parameters));
            endpoint.append(") {\n");
            endpoint.append("    boolean result = ").append(entityNameLower).append("Service.").append(methodName)
                    .append("(");
            endpoint.append(generateServiceCallParameters(parameters));
            endpoint.append(");\n");
            endpoint.append("    return ResponseEntity.ok(result);\n");
            endpoint.append("}");

        } else if (methodName.startsWith("count")) {
            // GET endpoint for count methods
            String pathParam = generatePathFromMethodName(methodName, entityNameLower);
            endpoint.append("@GetMapping(\"").append(pathParam).append("\")\n");
            endpoint.append("public ResponseEntity<Long> ");
            endpoint.append(methodName).append("(");
            endpoint.append(generateControllerParameters(parameters));
            endpoint.append(") {\n");
            endpoint.append("    long count = ").append(entityNameLower).append("Service.").append(methodName)
                    .append("(");
            endpoint.append(generateServiceCallParameters(parameters));
            endpoint.append(");\n");
            endpoint.append("    return ResponseEntity.ok(count);\n");
            endpoint.append("}");
        }
        return endpoint.toString();
    }

    /**
     * Extract method parameters from signature
     */
    private String extractMethodParameters(String signature) {
        int startParen = signature.indexOf('(');
        int endParen = signature.lastIndexOf(')');
        if (startParen > 0 && endParen > startParen) {
            return signature.substring(startParen + 1, endParen).trim();
        }
        return "";
    }

    /**
     * Generate REST path from method name
     */
    private String generatePathFromMethodName(String methodName, String entityNameLower) {
        if (methodName.startsWith("findBy") || methodName.startsWith("searchBy")) {
            String fieldName = methodName.substring(methodName.indexOf("By") + 2).toLowerCase();
            return "/search/" + fieldName + "/{value}";
        } else if (methodName.startsWith("existsBy")) {
            String fieldName = methodName.substring(methodName.indexOf("By") + 2).toLowerCase();
            return "/exists/" + fieldName + "/{value}";
        } else if (methodName.startsWith("countBy")) {
            String fieldName = methodName.substring(methodName.indexOf("By") + 2).toLowerCase();
            return "/count/" + fieldName + "/{value}";
        } else if (methodName.contains("search") || methodName.contains("find")) {
            return "/search";
        } else if (methodName.contains("validate")) {
            return "/validate";
        } else if (methodName.contains("count")) {
            return "/count";
        }
        return "/" + methodName.toLowerCase();
    }

    /**
     * Map service return type to controller return type
     */
    private String mapReturnTypeForController(String serviceReturnType) {
        if (serviceReturnType.contains("List")) {
            return serviceReturnType; // Keep List<EntityDto> as is
        } else if (serviceReturnType.contains("Optional")) {
            return serviceReturnType.replaceAll("Optional<(.+)>", "$1"); // Convert Optional<T> to T
        }
        return serviceReturnType;
    }

    /**
     * Generate controller method parameters
     */
    private String generateControllerParameters(String serviceParams) {
        if (serviceParams.isEmpty()) {
            return "";
        }

        StringBuilder controllerParams = new StringBuilder();
        String[] params = serviceParams.split(",");

        for (int i = 0; i < params.length; i++) {
            String param = params[i].trim();
            if (!param.isEmpty()) {
                String[] parts = param.split("\\s+");
                if (parts.length >= 2) {
                    String type = parts[0];
                    String name = parts[1];

                    // Add appropriate annotations
                    if (type.equals("String") && name.toLowerCase().contains("search")) {
                        controllerParams.append("@RequestParam String ").append(name);
                    } else {
                        controllerParams.append("@PathVariable ").append(type).append(" ").append(name);
                    }

                    if (i < params.length - 1) {
                        controllerParams.append(", ");
                    }
                }
            }
        }

        return controllerParams.toString();
    }

    /**
     * Generate service method call parameters
     */
    private String generateServiceCallParameters(String serviceParams) {
        if (serviceParams.isEmpty()) {
            return "";
        }

        StringBuilder callParams = new StringBuilder();
        String[] params = serviceParams.split(",");

        for (int i = 0; i < params.length; i++) {
            String param = params[i].trim();
            if (!param.isEmpty()) {
                String[] parts = param.split("\\s+");
                if (parts.length >= 2) {
                    String name = parts[1];
                    callParams.append(name);

                    if (i < params.length - 1) {
                        callParams.append(", ");
                    }
                }
            }
        }

        return callParams.toString();
    }

    /**
     * Generate controller method body
     */
    private String generateControllerMethodBody(String methodName, String entityNameLower, String returnType) {
        if (returnType.contains("List")) {
            return "List<" + extractGenericType(returnType) + "> result = " + entityNameLower + "Service." + methodName
                    + "(" +
                    generateServiceCallParameters("") + ");\n    return ResponseEntity.ok(result);";
        } else if (returnType.contains("Optional")) {
            return "return " + entityNameLower + "Service." + methodName + "(" +
                    generateServiceCallParameters("") + ")\n        .map(ResponseEntity::ok)\n        " +
                    ".orElse(ResponseEntity.notFound().build());";
        } else {
            return "return ResponseEntity.ok(" + entityNameLower + "Service." + methodName + "(" +
                    generateServiceCallParameters("") + "));";
        }
    }

    /**
     * Extract generic type from parameterized type
     */
    private String extractGenericType(String type) {
        int start = type.indexOf('<');
        int end = type.lastIndexOf('>');
        if (start > 0 && end > start) {
            return type.substring(start + 1, end);
        }
        return "Object";
    }

    /**
     * Add required imports to controller class
     */
    private String addRequiredImports(String content, String aiMethods) {
        StringBuilder imports = new StringBuilder();

        // Check if we need additional imports based on AI methods
        if (aiMethods.contains("stream") || aiMethods.contains("Collectors")) {
            if (!content.contains("import java.util.stream.Collectors;")) {
                imports.append("import java.util.stream.Collectors;\n");
            }
        }

        if (aiMethods.contains("@RequestParam") && !content.contains("@RequestParam")) {
            // This will be added by the endpoint generation, so we ensure the import exists
            if (!content.contains("import org.springframework.web.bind.annotation.RequestParam;")) {
                imports.append("import org.springframework.web.bind.annotation.RequestParam;\n");
            }
        }

        // Add imports after existing imports
        if (imports.length() > 0) {
            int lastImportIndex = content.lastIndexOf("import ");
            if (lastImportIndex > 0) {
                int endOfLastImport = content.indexOf('\n', lastImportIndex);
                if (endOfLastImport > 0) {
                    StringBuilder newContent = new StringBuilder();
                    newContent.append(content.substring(0, endOfLastImport + 1));
                    newContent.append(imports.toString());
                    newContent.append(content.substring(endOfLastImport + 1));
                    return newContent.toString();
                }
            }
        }
        return content;
    }

    /**
     * Add AI-generated controller endpoints based on service methods
     */
    private void addAIEndpointsToController(Path projectBasePath, String packageName,
            EntityDefinition entityDef, String aiMethods) throws Exception {
        try {
            String entityName = entityDef.getName();
            String controllerFileName = entityName + "Controller.java";

            // Find the controller file
            Path controllerPath = projectBasePath
                    .resolve("src/main/java")
                    .resolve(packageName.replace('.', '/'))
                    .resolve("controller")
                    .resolve(controllerFileName);

            if (!Files.exists(controllerPath)) {
                System.out.println("⚠ Warning: Controller file not found: " + controllerPath);
                return;
            }

            // Read the current file content
            String currentContent = Files.readString(controllerPath);

            // Generate controller endpoints from AI service methods
            String controllerEndpoints = generateControllerEndpoints(aiMethods, entityDef);

            if (controllerEndpoints.trim().isEmpty()) {
                return;
            }

            // Find the position before the last closing brace
            int lastBraceIndex = currentContent.lastIndexOf('}');
            if (lastBraceIndex == -1) {
                System.out.println("⚠ Warning: Could not find closing brace in controller");
                return;
            }

            // Add necessary imports if not present
            String updatedContent = addControllerImports(currentContent);

            // Insert the controller endpoints before the last closing brace
            StringBuilder finalContent = new StringBuilder();
            int newLastBraceIndex = updatedContent.lastIndexOf('}');
            finalContent.append(updatedContent.substring(0, newLastBraceIndex));
            finalContent.append("\n    // AI-Generated Controller Endpoints\n");
            finalContent.append("    ").append(controllerEndpoints.replace("\n", "\n    "));
            finalContent.append("\n");
            finalContent.append(updatedContent.substring(newLastBraceIndex));

            // Write the updated content back to the file
            Files.writeString(controllerPath, finalContent.toString());

        } catch (Exception e) {
            throw new GenerationException("AI Controller Endpoints",
                    "Failed to add AI endpoints to " + entityDef.getName() + "Controller",
                    "Error modifying controller: " + e.getMessage(), e);
        }
    }

    /**
     * Generate controller endpoints from AI service methods
     */
    private String generateControllerEndpoints(String aiMethods, EntityDefinition entityDef) {
        StringBuilder endpoints = new StringBuilder();

        // Clean up the AI methods
        String cleanMethods = aiMethods.trim();
        if (cleanMethods.startsWith("```java")) {
            cleanMethods = cleanMethods.substring(7);
        }
        if (cleanMethods.endsWith("```")) {
            cleanMethods = cleanMethods.substring(0, cleanMethods.length() - 3);
        }

        // Split by lines and extract method signatures
        String[] lines = cleanMethods.split("\n");
        String entityName = entityDef.getName();
        String entityLowercase = entityName.toLowerCase();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Look for method signatures (public methods)
            if (line.contains("public") && line.contains("(") && line.contains(")")) {
                String methodSignature = extractMethodSignatureFromLine(line);
                if (!methodSignature.isEmpty()) {
                    String endpoint = generateEndpointFromMethod(methodSignature, entityDef);
                    if (!endpoint.isEmpty()) {
                        endpoints.append(endpoint).append("\n\n");
                    }
                }
            }
        }

        return endpoints.toString();
    }

    /**
     * Generate REST endpoint from service method
     */
    private String generateEndpointFromMethod(String methodSignature, EntityDefinition entityDef) {
        String entityName = entityDef.getName();
        String serviceName = entityName.toLowerCase() + "Service";

        // Extract method name and parameters
        String methodName = extractMethodName(methodSignature);
        String returnType = extractReturnType(methodSignature);
        String parameters = extractParameters(methodSignature);

        if (methodName.isEmpty()) {
            return "";
        }

        StringBuilder endpoint = new StringBuilder();

        // Determine HTTP method and path based on method name patterns
        String httpMethod = "GET"; // Default to GET for most query methods
        String path = "/";

        if (methodName.startsWith("find") || methodName.startsWith("search") || methodName.startsWith("get")) {
            httpMethod = "GET";
            path = "/search/" + methodName.replace("find", "").replace("search", "").replace("get", "").toLowerCase();
        } else if (methodName.startsWith("exists") || methodName.startsWith("count")) {
            httpMethod = "GET";
            path = "/" + methodName.toLowerCase();
        } else if (methodName.startsWith("validate")) {
            httpMethod = "POST";
            path = "/validate";
        } else {
            // Generic endpoint
            path = "/" + methodName.toLowerCase();
        }

        // Generate the endpoint
        endpoint.append("@").append(httpMethod.equals("GET") ? "GetMapping" : "PostMapping")
                .append("(\"").append(path).append("\")\n");

        // Generate method signature
        endpoint.append("public ResponseEntity<").append(returnType).append("> ")
                .append(methodName).append("(");

        // Add parameters with appropriate annotations
        if (!parameters.isEmpty()) {
            String[] paramArray = parameters.split(",");
            for (int i = 0; i < paramArray.length; i++) {
                String param = paramArray[i].trim();
                if (!param.isEmpty()) {
                    // Add @RequestParam for GET methods, @RequestBody for POST methods
                    if (httpMethod.equals("GET")) {
                        endpoint.append("@RequestParam ");
                    } else {
                        endpoint.append("@RequestBody ");
                    }
                    endpoint.append(param);
                    if (i < paramArray.length - 1) {
                        endpoint.append(", ");
                    }
                }
            }
        }

        endpoint.append(") {\n");
        endpoint.append("    ");

        // Generate method body
        if (returnType.contains("List") || returnType.contains("Collection")) {
            endpoint.append("List<").append(entityName).append("Dto> result = ");
        } else if (returnType.contains("Optional")) {
            endpoint.append("Optional<").append(entityName).append("Dto> result = ");
        } else if (returnType.equals("boolean")) {
            endpoint.append("boolean result = ");
        } else {
            endpoint.append(returnType).append(" result = ");
        }

        // Call service method
        endpoint.append(serviceName).append(".").append(methodName).append("(");
        if (!parameters.isEmpty()) {
            String[] paramArray = parameters.split(",");
            for (int i = 0; i < paramArray.length; i++) {
                String param = paramArray[i].trim();
                if (!param.isEmpty()) {
                    String paramName = param.substring(param.lastIndexOf(' ') + 1);
                    endpoint.append(paramName);
                    if (i < paramArray.length - 1) {
                        endpoint.append(", ");
                    }
                }
            }
        }
        endpoint.append(");\n");

        // Generate return statement
        if (returnType.contains("Optional")) {
            endpoint.append("    return result.map(ResponseEntity::ok)\n")
                    .append("            .orElse(ResponseEntity.notFound().build());\n");
        } else if (returnType.equals("boolean")) {
            endpoint.append("    return ResponseEntity.ok(result);\n");
        } else {
            endpoint.append("    return ResponseEntity.ok(result);\n");
        }

        endpoint.append("}");

        return endpoint.toString();
    }

    /**
     * Extract method name from method signature
     */
    private String extractMethodName(String signature) {
        // Look for pattern: returnType methodName(
        int parenIndex = signature.indexOf('(');
        if (parenIndex == -1)
            return "";

        String beforeParen = signature.substring(0, parenIndex).trim();
        String[] parts = beforeParen.split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 1]; // Last part before parentheses
        }
        return "";
    }

    /**
     * Extract return type from method signature
     */
    private String extractReturnType(String signature) {
        // Look for pattern: public returnType methodName(
        String[] parts = signature.trim().split("\\s+");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("public") || parts[i].equals("private") || parts[i].equals("protected")) {
                if (i + 1 < parts.length) {
                    String returnType = parts[i + 1];
                    // Handle generic types
                    if (returnType.contains("<")) {
                        int parenIndex = signature.indexOf('(');
                        String beforeParen = signature.substring(0, parenIndex);
                        int publicIndex = beforeParen.indexOf("public");
                        if (publicIndex != -1) {
                            String afterPublic = beforeParen.substring(publicIndex + 6).trim();
                            int lastSpaceIndex = afterPublic.lastIndexOf(' ');
                            if (lastSpaceIndex != -1) {
                                return afterPublic.substring(0, lastSpaceIndex).trim();
                            }
                        }
                    }
                    return returnType;
                }
            }
        }
        return "Object"; // Default
    }

    /**
     * Extract parameters from method signature
     */
    private String extractParameters(String signature) {
        int startParen = signature.indexOf('(');
        int endParen = signature.lastIndexOf(')');
        if (startParen == -1 || endParen == -1 || endParen <= startParen) {
            return "";
        }
        return signature.substring(startParen + 1, endParen).trim();
    }

    /**
     * Extract method signature from a line
     */
    private String extractMethodSignatureFromLine(String line) {
        String trimmed = line.trim();
        int braceIndex = trimmed.indexOf('{');
        if (braceIndex > 0) {
            return trimmed.substring(0, braceIndex).trim();
        }
        return trimmed;
    }

    /**
     * Add necessary imports to controller file
     */
    private String addControllerImports(String content) {
        StringBuilder result = new StringBuilder(content);

        // List of imports that might be needed for AI-generated endpoints
        String[] potentialImports = {
                "import org.springframework.web.bind.annotation.RequestParam;",
                "import org.springframework.web.bind.annotation.RequestBody;",
                "import org.springframework.web.bind.annotation.GetMapping;",
                "import org.springframework.web.bind.annotation.PostMapping;",
                "import java.util.Optional;"
        };

        // Find the last import statement
        int lastImportIndex = content.lastIndexOf("import ");
        if (lastImportIndex != -1) {
            int endOfLastImport = content.indexOf('\n', lastImportIndex);
            if (endOfLastImport != -1) {
                StringBuilder imports = new StringBuilder();

                // Add missing imports
                for (String importStmt : potentialImports) {
                    if (!content.contains(importStmt)) {
                        imports.append(importStmt).append("\n");
                    }
                }

                if (imports.length() > 0) {
                    result.insert(endOfLastImport + 1, imports.toString());
                }
            }
        }

        return result.toString();
    }
}
