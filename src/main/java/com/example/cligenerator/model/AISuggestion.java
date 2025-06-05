package com.example.cligenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISuggestion {
    private boolean compatible;
    private String explanation;
    private String recommendedJavaVersion;
    private String recommendedSpringBootVersion;
}
