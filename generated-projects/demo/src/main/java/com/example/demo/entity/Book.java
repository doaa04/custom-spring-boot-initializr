package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "Title cannot be blank")
            @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
        private String title;
            @NotNull(message = "Price cannot be null")
        private Double price;
        private Author author;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}