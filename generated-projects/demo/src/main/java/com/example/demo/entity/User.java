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

            @NotBlank(message = "Username cannot be blank")
            @Size(min = 2, max = 255, message = "Username must be between 2 and 255 characters")
        private String username;
            @NotBlank(message = "Email cannot be blank")
            @Size(min = 2, max = 255, message = "Email must be between 2 and 255 characters")
        private String email;
            @NotBlank(message = "PasswordHash cannot be blank")
            @Size(min = 2, max = 255, message = "PasswordHash must be between 2 and 255 characters")
        private String passwordHash;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}