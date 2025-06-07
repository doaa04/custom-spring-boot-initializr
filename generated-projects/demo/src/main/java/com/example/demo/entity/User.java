package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "Name cannot be blank")
            @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        private String name;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}