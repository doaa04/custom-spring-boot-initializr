package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

private Long id;

        private String roomNumber;
        private String type;
        private Boolean isAvailable;
        private Long departmentId;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}