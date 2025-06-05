package com.example.demo.service;

import com.example.demo.dto.ReviewDto;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
List<ReviewDto> findAll();
    Optional<ReviewDto> findById(Long id);
        ReviewDto save(ReviewDto reviewDto);
        Optional<ReviewDto> update(Long id, ReviewDto reviewDto);
            boolean deleteById(Long id);
            }