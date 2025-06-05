package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
    import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "FirstName cannot be blank")
            @Size(min = 2, max = 255, message = "FirstName must be between 2 and 255 characters")
        private String firstName;
            @NotBlank(message = "LastName cannot be blank")
            @Size(min = 2, max = 255, message = "LastName must be between 2 and 255 characters")
        private String lastName;
            @NotNull(message = "DateOfBirth cannot be null")
            @PastOrPresent(message = "DateOfBirth must be in the past or present")
        private LocalDate dateOfBirth;
            @NotBlank(message = "Gender cannot be blank")
            @Size(min = 2, max = 255, message = "Gender must be between 2 and 255 characters")
        private String gender;
            @NotBlank(message = "PhoneNumber cannot be blank")
            @Size(min = 2, max = 255, message = "PhoneNumber must be between 2 and 255 characters")
        private String phoneNumber;
            @NotBlank(message = "Email cannot be blank")
            @Size(min = 2, max = 255, message = "Email must be between 2 and 255 characters")
        private String email;
            @NotBlank(message = "Address cannot be blank")
            @Size(min = 2, max = 255, message = "Address must be between 2 and 255 characters")
        private String address;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}