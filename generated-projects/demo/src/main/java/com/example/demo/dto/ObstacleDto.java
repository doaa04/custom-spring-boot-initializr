package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObstacleDto {

private Long id;

        private String type;
        private Double positionX;
        private Double positionY;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}