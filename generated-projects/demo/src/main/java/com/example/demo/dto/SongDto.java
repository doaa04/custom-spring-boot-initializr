package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {

private Long id;

        private String title;
        private String artist;
        private String album;
        private Integer duration;
        private Integer trackNumber;
        private Integer releaseYear;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}