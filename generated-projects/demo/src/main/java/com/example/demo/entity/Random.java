package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "random")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Random {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}