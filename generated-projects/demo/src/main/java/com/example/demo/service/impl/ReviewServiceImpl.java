package com.example.demo.service.impl;

import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class ReviewServiceImpl implements ReviewService {

private final ReviewRepository reviewRepository;

public ReviewServiceImpl(ReviewRepository reviewRepository) {
this.reviewRepository = reviewRepository;
}

@Override
@Transactional(readOnly = true)
public List<ReviewDto> findAll() {
    return reviewRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewDto> findById(Long id) {
        return reviewRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public ReviewDto save(ReviewDto reviewDto) {
        Review review = convertToEntity(reviewDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (reviewDto.getId() == null) {
        review.setId(null); // Ensure it's null for auto-generation
        }
        review = reviewRepository.save(review);
        return convertToDto(review);
        }

        @Override
        public Optional<ReviewDto> update(Long id, ReviewDto reviewDto) {
            return reviewRepository.findById(id)
            .map(existingReview -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (reviewDto.getBookId() != null) { // Simple null check
                    existingReview.setBookId(reviewDto.getBookId());
                    }
                    if (reviewDto.getUserId() != null) { // Simple null check
                    existingReview.setUserId(reviewDto.getUserId());
                    }
                    if (reviewDto.getRating() != null) { // Simple null check
                    existingReview.setRating(reviewDto.getRating());
                    }
                    if (reviewDto.getComment() != null) { // Simple null check
                    existingReview.setComment(reviewDto.getComment());
                    }
                    if (reviewDto.getReviewDate() != null) { // Simple null check
                    existingReview.setReviewDate(reviewDto.getReviewDate());
                    }
            Review updatedReview = reviewRepository.save(existingReview);
            return convertToDto(updatedReview);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private ReviewDto convertToDto(Review review) {
            ReviewDto dto = new ReviewDto();
            BeanUtils.copyProperties(review, dto);
            return dto;
            }

            private Review convertToEntity(ReviewDto reviewDto) {
            Review entity = new Review();
            BeanUtils.copyProperties(reviewDto, entity);
            return entity;
            }
            }