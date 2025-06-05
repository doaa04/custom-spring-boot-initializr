package com.example.demo.controller;

import com.example.demo.dto.AuthorDto;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/authors") // Simple pluralization
public class AuthorController {

private final AuthorService authorService;

public AuthorController(AuthorService authorService) {
this.authorService = authorService;
}

@GetMapping
public ResponseEntity<List<AuthorDto>> getAllAuthors() {
    return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        return authorService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (authorDto.getId() != null) {
            authorDto.setId(null);
            }
            AuthorDto savedDto = authorService.save(authorDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (authorDto.getId() == null) {
                authorDto.setId(id);
                } else if (!authorDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return authorService.update(id, authorDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
                    if (authorService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with id " + id);
                    }
                    }
                    }