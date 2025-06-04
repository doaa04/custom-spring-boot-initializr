package com.example.cligenerator.service;

import com.example.cligenerator.model.ProjectDescription;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class GitService {
    private final ReadmeService readmeService;

    public GitService(ReadmeService readmeService) {
        this.readmeService = readmeService;
    }

    public void initPush(Path projectBasePath, ProjectDescription description) {
        String projectName = description.getProjectName();
        String remoteUrl = description.getGithubRemoteUrl();

        try {
            // Initialize Git
            runCommand(projectBasePath, "git", "init");

            // Create README
            String readme = "#" + projectName;
            if (description.isSupportingAI()) {
                readme = readmeService.generate(description);
            }
            Files.writeString(projectBasePath.resolve("README.md"), readme);

            // Add all files (respects .gitignore)
            runCommand(projectBasePath, "git", "add", ".");

            // Commit
            runCommand(projectBasePath, "git", "commit", "-m", "Initial commit");

            // Rename branch
            runCommand(projectBasePath, "git", "branch", "-M", "main");

            // Add remote
            runCommand(projectBasePath, "git", "remote", "add", "origin", remoteUrl);

            // Push
            runCommand(projectBasePath, "git", "push", "-u", "origin", "main");

        } catch (IOException | InterruptedException e) {
            System.err.println("Git operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runCommand(Path workingDir, String... command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command)
                .directory(workingDir.toFile())
                .redirectErrorStream(true);

        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed: " + String.join(" ", command));
        }
    }
}