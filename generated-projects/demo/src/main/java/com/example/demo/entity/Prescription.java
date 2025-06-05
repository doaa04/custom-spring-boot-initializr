package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "prescription")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "DateIssued cannot be null")
            @PastOrPresent(message = "DateIssued must be in the past or present")
        private LocalDate dateIssued;
            @NotBlank(message = "MedicationDetails cannot be blank")
            @Size(min = 2, max = 255, message = "MedicationDetails must be between 2 and 255 characters")
        private String medicationDetails;
            @NotBlank(message = "DosageInstructions cannot be blank")
            @Size(min = 2, max = 255, message = "DosageInstructions must be between 2 and 255 characters")
        private String dosageInstructions;
            @NotNull(message = "PatientId cannot be null")
        private Long patientId;
            @NotNull(message = "DoctorId cannot be null")
        private Long doctorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}