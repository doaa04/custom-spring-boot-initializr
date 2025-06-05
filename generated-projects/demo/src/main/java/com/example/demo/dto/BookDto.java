package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

private Long id;

        private String title;
        private Double price;
        private Author author;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}