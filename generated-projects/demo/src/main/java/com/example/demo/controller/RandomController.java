package com.example.demo.controller;

import com.example.demo.dto.RandomDto;
import com.example.demo.service.RandomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/randoms") // Simple pluralization
public class RandomController {

private final RandomService randomService;

public RandomController(RandomService randomService) {
this.randomService = randomService;
}

@GetMapping
public ResponseEntity<List<RandomDto>> getAllRandoms() {
    return ResponseEntity.ok(randomService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RandomDto> getRandomById(@PathVariable Long id) {
        return randomService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Random not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<RandomDto> createRandom(@Valid @RequestBody RandomDto randomDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (randomDto.getId() != null) {
            randomDto.setId(null);
            }
            RandomDto savedDto = randomService.save(randomDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<RandomDto> updateRandom(@PathVariable Long id, @Valid @RequestBody RandomDto randomDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (randomDto.getId() == null) {
                randomDto.setId(id);
                } else if (!randomDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return randomService.update(id, randomDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Random not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteRandom(@PathVariable Long id) {
                    if (randomService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Random not found with id " + id);
                    }
                    }
                    }