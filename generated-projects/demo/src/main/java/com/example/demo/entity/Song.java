package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "song")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotBlank(message = "Title cannot be blank")
            @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
        private String title;
            @NotBlank(message = "Artist cannot be blank")
            @Size(min = 2, max = 255, message = "Artist must be between 2 and 255 characters")
        private String artist;
            @NotBlank(message = "Album cannot be blank")
            @Size(min = 2, max = 255, message = "Album must be between 2 and 255 characters")
        private String album;
            @NotNull(message = "Duration cannot be null")
        private Integer duration;
            @NotNull(message = "TrackNumber cannot be null")
        private Integer trackNumber;
            @NotNull(message = "ReleaseYear cannot be null")
        private Integer releaseYear;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}