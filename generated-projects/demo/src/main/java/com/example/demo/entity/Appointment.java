package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "appointment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "AppointmentDate cannot be null")
            @PastOrPresent(message = "AppointmentDate must be in the past or present")
        private LocalDate appointmentDate;
            @NotBlank(message = "AppointmentTime cannot be blank")
            @Size(min = 2, max = 255, message = "AppointmentTime must be between 2 and 255 characters")
        private String appointmentTime;
            @NotBlank(message = "Reason cannot be blank")
            @Size(min = 2, max = 255, message = "Reason must be between 2 and 255 characters")
        private String reason;
            @NotBlank(message = "Status cannot be blank")
            @Size(min = 2, max = 255, message = "Status must be between 2 and 255 characters")
        private String status;
            @NotNull(message = "PatientId cannot be null")
        private Long patientId;
            @NotNull(message = "DoctorId cannot be null")
        private Long doctorId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}