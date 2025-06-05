package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "Amount cannot be null")
        private Double amount;
            @NotNull(message = "IssueDate cannot be null")
            @PastOrPresent(message = "IssueDate must be in the past or present")
        private LocalDate issueDate;
            @NotNull(message = "DueDate cannot be null")
            @PastOrPresent(message = "DueDate must be in the past or present")
        private LocalDate dueDate;
            @NotBlank(message = "Status cannot be blank")
            @Size(min = 2, max = 255, message = "Status must be between 2 and 255 characters")
        private String status;
            @NotNull(message = "PatientId cannot be null")
        private Long patientId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}