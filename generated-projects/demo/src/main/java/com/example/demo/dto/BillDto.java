package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDto {

private Long id;

        private Double amount;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private String status;
        private Long patientId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}