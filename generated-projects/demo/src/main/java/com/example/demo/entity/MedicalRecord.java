package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "medicalrecord")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "RecordDate cannot be null")
            @PastOrPresent(message = "RecordDate must be in the past or present")
        private LocalDate recordDate;
            @NotBlank(message = "Description cannot be blank")
            @Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
        private String description;
            @NotBlank(message = "Diagnosis cannot be blank")
            @Size(min = 2, max = 255, message = "Diagnosis must be between 2 and 255 characters")
        private String diagnosis;
            @NotBlank(message = "Treatment cannot be blank")
            @Size(min = 2, max = 255, message = "Treatment must be between 2 and 255 characters")
        private String treatment;
            @NotNull(message = "PatientId cannot be null")
        private Long patientId;
            @NotNull(message = "DoctorId cannot be null")
        private Long doctorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}