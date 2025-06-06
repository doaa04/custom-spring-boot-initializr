package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "obstacle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Obstacle {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "Type cannot be blank")
            @Size(min = 2, max = 255, message = "Type must be between 2 and 255 characters")
        private String type;
            @NotNull(message = "PositionX cannot be null")
        private Double positionX;
            @NotNull(message = "PositionY cannot be null")
        private Double positionY;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}