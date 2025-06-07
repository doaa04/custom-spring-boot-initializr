package com.example.demo.controller;

import com.example.demo.dto.SongDto;
import com.example.demo.service.SongService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/songs") // Simple pluralization
public class SongController {

private final SongService songService;

public SongController(SongService songService) {
this.songService = songService;
}

@GetMapping
public ResponseEntity<List<SongDto>> getAllSongs() {
    return ResponseEntity.ok(songService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable Long id) {
        return songService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<SongDto> createSong(@Valid @RequestBody SongDto songDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (songDto.getId() != null) {
            songDto.setId(null);
            }
            SongDto savedDto = songService.save(songDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<SongDto> updateSong(@PathVariable Long id, @Valid @RequestBody SongDto songDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (songDto.getId() == null) {
                songDto.setId(id);
                } else if (!songDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return songService.update(id, songDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
                    if (songService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found with id " + id);
                    }
                    }
                    }