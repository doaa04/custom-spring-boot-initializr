package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

private Long id;

        private LocalDate appointmentDate;
        private String appointmentTime;
        private String reason;
        private String status;
        private Long patientId;
        private Long doctorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}