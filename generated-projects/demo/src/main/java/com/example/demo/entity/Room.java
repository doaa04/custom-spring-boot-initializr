package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "RoomNumber cannot be blank")
            @Size(min = 2, max = 255, message = "RoomNumber must be between 2 and 255 characters")
        private String roomNumber;
            @NotBlank(message = "Type cannot be blank")
            @Size(min = 2, max = 255, message = "Type must be between 2 and 255 characters")
        private String type;
        private Boolean isAvailable;
            @NotNull(message = "DepartmentId cannot be null")
        private Long departmentId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}