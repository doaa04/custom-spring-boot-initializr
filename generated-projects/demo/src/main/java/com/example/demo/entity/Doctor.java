package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "doctor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "FirstName cannot be blank")
            @Size(min = 2, max = 255, message = "FirstName must be between 2 and 255 characters")
        private String firstName;
            @NotBlank(message = "LastName cannot be blank")
            @Size(min = 2, max = 255, message = "LastName must be between 2 and 255 characters")
        private String lastName;
            @NotBlank(message = "Specialization cannot be blank")
            @Size(min = 2, max = 255, message = "Specialization must be between 2 and 255 characters")
        private String specialization;
            @NotBlank(message = "Email cannot be blank")
            @Size(min = 2, max = 255, message = "Email must be between 2 and 255 characters")
        private String email;
            @NotBlank(message = "PhoneNumber cannot be blank")
            @Size(min = 2, max = 255, message = "PhoneNumber must be between 2 and 255 characters")
        private String phoneNumber;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}