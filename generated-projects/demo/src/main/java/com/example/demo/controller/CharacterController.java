package com.example.demo.controller;

import com.example.demo.dto.CharacterDto;
import com.example.demo.service.CharacterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/characters") // Simple pluralization
public class CharacterController {

private final CharacterService characterService;

public CharacterController(CharacterService characterService) {
this.characterService = characterService;
}

@GetMapping
public ResponseEntity<List<CharacterDto>> getAllCharacters() {
    return ResponseEntity.ok(characterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CharacterDto> getCharacterById(@PathVariable Long id) {
        return characterService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<CharacterDto> createCharacter(@Valid @RequestBody CharacterDto characterDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (characterDto.getId() != null) {
            characterDto.setId(null);
            }
            CharacterDto savedDto = characterService.save(characterDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<CharacterDto> updateCharacter(@PathVariable Long id, @Valid @RequestBody CharacterDto characterDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (characterDto.getId() == null) {
                characterDto.setId(id);
                } else if (!characterDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return characterService.update(id, characterDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
                    if (characterService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found with id " + id);
                    }
                    }
                    }