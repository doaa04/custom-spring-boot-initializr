package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {

private Long id;

        private String name;
        private String description;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}