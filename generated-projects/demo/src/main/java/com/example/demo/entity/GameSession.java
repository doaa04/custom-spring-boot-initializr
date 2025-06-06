package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "gamesession")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "StartTime cannot be null")
            @PastOrPresent(message = "StartTime must be in the past or present")
        private LocalDate startTime;
            @NotNull(message = "EndTime cannot be null")
            @PastOrPresent(message = "EndTime must be in the past or present")
        private LocalDate endTime;
            @NotNull(message = "FinalScore cannot be null")
        private Integer finalScore;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}