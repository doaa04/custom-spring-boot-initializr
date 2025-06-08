package com.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;

    private String name;
    private Integer budget;

    // Lombok will generate getters, setters, constructor, toString, equals,
    // hashCode
}