package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

private Long id;

        private String firstName;
        private String lastName;
        private String specialization;
        private String email;
        private String phoneNumber;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}