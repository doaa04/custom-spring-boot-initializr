package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {

private Long id;

        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String gender;
        private String phoneNumber;
        private String email;
        private String address;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}