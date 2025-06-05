package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {

private Long id;

        private LocalDate dateIssued;
        private String medicationDetails;
        private String dosageInstructions;
        private Long patientId;
        private Long doctorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}