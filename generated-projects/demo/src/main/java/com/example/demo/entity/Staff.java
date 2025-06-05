package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "FirstName cannot be blank")
            @Size(min = 2, max = 255, message = "FirstName must be between 2 and 255 characters")
        private String firstName;
            @NotBlank(message = "LastName cannot be blank")
            @Size(min = 2, max = 255, message = "LastName must be between 2 and 255 characters")
        private String lastName;
            @NotBlank(message = "Role cannot be blank")
            @Size(min = 2, max = 255, message = "Role must be between 2 and 255 characters")
        private String role;
            @NotBlank(message = "Email cannot be blank")
            @Size(min = 2, max = 255, message = "Email must be between 2 and 255 characters")
        private String email;
            @NotBlank(message = "PhoneNumber cannot be blank")
            @Size(min = 2, max = 255, message = "PhoneNumber must be between 2 and 255 characters")
        private String phoneNumber;
            @NotNull(message = "DepartmentId cannot be null")
        private Long departmentId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}