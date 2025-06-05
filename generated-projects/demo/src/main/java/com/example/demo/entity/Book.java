package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

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
            @NotBlank(message = "Isbn cannot be blank")
            @Size(min = 2, max = 255, message = "Isbn must be between 2 and 255 characters")
        private String isbn;
            @NotNull(message = "PublicationDate cannot be null")
            @PastOrPresent(message = "PublicationDate must be in the past or present")
        private LocalDate publicationDate;
            @NotNull(message = "Price cannot be null")
        private Double price;
            @NotNull(message = "AuthorId cannot be null")
        private Long authorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}