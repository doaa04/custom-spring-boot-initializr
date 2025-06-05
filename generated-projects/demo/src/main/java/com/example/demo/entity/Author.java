package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "author")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "FirstName cannot be blank")
            @Size(min = 2, max = 255, message = "FirstName must be between 2 and 255 characters")
        private String firstName;
            @NotBlank(message = "LastName cannot be blank")
            @Size(min = 2, max = 255, message = "LastName must be between 2 and 255 characters")
        private String lastName;
            @NotBlank(message = "Bio cannot be blank")
            @Size(min = 2, max = 255, message = "Bio must be between 2 and 255 characters")
        private String bio;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}