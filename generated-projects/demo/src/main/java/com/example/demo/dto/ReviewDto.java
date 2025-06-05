package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

private Long id;

        private Long bookId;
        private Long userId;
        private Integer rating;
        private String comment;
        private LocalDate reviewDate;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}