package com.example.cligenerator.controller;

import com.example.cligenerator.model.AISuggestion;
import com.example.cligenerator.model.ProjectDescription;
import com.example.cligenerator.service.DependencyCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api")
public class DependencyCheckController {
    private final DependencyCheckService dependencyCheckService;

    public DependencyCheckController(DependencyCheckService dependencyCheckService) {
        this.dependencyCheckService = dependencyCheckService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AISuggestion> analyze(@RequestBody ProjectDescription description) {
        AISuggestion suggestion = dependencyCheckService.analyze(description);
        return ResponseEntity.ok(suggestion);
    }
}
