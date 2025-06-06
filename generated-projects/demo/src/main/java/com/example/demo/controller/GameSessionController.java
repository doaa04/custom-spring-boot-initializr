package com.example.demo.controller;

import com.example.demo.dto.GameSessionDto;
import com.example.demo.service.GameSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/gamesessions") // Simple pluralization
public class GameSessionController {

private final GameSessionService gamesessionService;

public GameSessionController(GameSessionService gamesessionService) {
this.gamesessionService = gamesessionService;
}

@GetMapping
public ResponseEntity<List<GameSessionDto>> getAllGameSessions() {
    return ResponseEntity.ok(gamesessionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameSessionDto> getGameSessionById(@PathVariable Long id) {
        return gamesessionService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<GameSessionDto> createGameSession(@Valid @RequestBody GameSessionDto gamesessionDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (gamesessionDto.getId() != null) {
            gamesessionDto.setId(null);
            }
            GameSessionDto savedDto = gamesessionService.save(gamesessionDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<GameSessionDto> updateGameSession(@PathVariable Long id, @Valid @RequestBody GameSessionDto gamesessionDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (gamesessionDto.getId() == null) {
                gamesessionDto.setId(id);
                } else if (!gamesessionDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return gamesessionService.update(id, gamesessionDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteGameSession(@PathVariable Long id) {
                    if (gamesessionService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GameSession not found with id " + id);
                    }
                    }
                    }