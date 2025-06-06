package com.example.demo.controller;

import com.example.demo.dto.ObstacleDto;
import com.example.demo.service.ObstacleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/obstacles") // Simple pluralization
public class ObstacleController {

private final ObstacleService obstacleService;

public ObstacleController(ObstacleService obstacleService) {
this.obstacleService = obstacleService;
}

@GetMapping
public ResponseEntity<List<ObstacleDto>> getAllObstacles() {
    return ResponseEntity.ok(obstacleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObstacleDto> getObstacleById(@PathVariable Long id) {
        return obstacleService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obstacle not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<ObstacleDto> createObstacle(@Valid @RequestBody ObstacleDto obstacleDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (obstacleDto.getId() != null) {
            obstacleDto.setId(null);
            }
            ObstacleDto savedDto = obstacleService.save(obstacleDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<ObstacleDto> updateObstacle(@PathVariable Long id, @Valid @RequestBody ObstacleDto obstacleDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (obstacleDto.getId() == null) {
                obstacleDto.setId(id);
                } else if (!obstacleDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return obstacleService.update(id, obstacleDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obstacle not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteObstacle(@PathVariable Long id) {
                    if (obstacleService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Obstacle not found with id " + id);
                    }
                    }
                    }