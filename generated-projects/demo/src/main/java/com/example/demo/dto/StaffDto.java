package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {

private Long id;

        private String firstName;
        private String lastName;
        private String role;
        private String email;
        private String phoneNumber;
        private Long departmentId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}