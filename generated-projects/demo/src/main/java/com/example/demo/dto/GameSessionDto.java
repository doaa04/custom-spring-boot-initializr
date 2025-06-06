package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionDto {

private Long id;

        private LocalDate startTime;
        private LocalDate endTime;
        private Integer finalScore;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}