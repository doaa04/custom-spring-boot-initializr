package com.example.demo.controller;

import com.example.demo.dto.PlaylistDto;
import com.example.demo.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/playlists") // Simple pluralization
public class PlaylistController {

private final PlaylistService playlistService;

public PlaylistController(PlaylistService playlistService) {
this.playlistService = playlistService;
}

@GetMapping
public ResponseEntity<List<PlaylistDto>> getAllPlaylists() {
    return ResponseEntity.ok(playlistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable Long id) {
        return playlistService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<PlaylistDto> createPlaylist(@Valid @RequestBody PlaylistDto playlistDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (playlistDto.getId() != null) {
            playlistDto.setId(null);
            }
            PlaylistDto savedDto = playlistService.save(playlistDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<PlaylistDto> updatePlaylist(@PathVariable Long id, @Valid @RequestBody PlaylistDto playlistDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (playlistDto.getId() == null) {
                playlistDto.setId(id);
                } else if (!playlistDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return playlistService.update(id, playlistDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
                    if (playlistService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found with id " + id);
                    }
                    }
                    }