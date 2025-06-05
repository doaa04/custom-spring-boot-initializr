package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/reviews") // Simple pluralization
public class ReviewController {

private final ReviewService reviewService;

public ReviewController(ReviewService reviewService) {
this.reviewService = reviewService;
}

@GetMapping
public ResponseEntity<List<ReviewDto>> getAllReviews() {
    return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (reviewDto.getId() != null) {
            reviewDto.setId(null);
            }
            ReviewDto savedDto = reviewService.save(reviewDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<ReviewDto> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto reviewDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (reviewDto.getId() == null) {
                reviewDto.setId(id);
                } else if (!reviewDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return reviewService.update(id, reviewDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
                    if (reviewService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found with id " + id);
                    }
                    }
                    }