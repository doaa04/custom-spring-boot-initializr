package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "BookId cannot be null")
        private Long bookId;
            @NotNull(message = "UserId cannot be null")
        private Long userId;
            @NotNull(message = "Rating cannot be null")
        private Integer rating;
            @NotBlank(message = "Comment cannot be blank")
            @Size(min = 2, max = 255, message = "Comment must be between 2 and 255 characters")
        private String comment;
            @NotNull(message = "ReviewDate cannot be null")
            @PastOrPresent(message = "ReviewDate must be in the past or present")
        private LocalDate reviewDate;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}