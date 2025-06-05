package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/books") // Simple pluralization
public class BookController {

private final BookService bookService;

public BookController(BookService bookService) {
this.bookService = bookService;
}

@GetMapping
public ResponseEntity<List<BookDto>> getAllBooks() {
    return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return bookService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (bookDto.getId() != null) {
            bookDto.setId(null);
            }
            BookDto savedDto = bookService.save(bookDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (bookDto.getId() == null) {
                bookDto.setId(id);
                } else if (!bookDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return bookService.update(id, bookDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
                    if (bookService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id " + id);
                    }
                    }
                    }