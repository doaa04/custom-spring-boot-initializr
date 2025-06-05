package com.example.demo.service;

import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Review;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Review Service Unit Tests")
class ReviewServiceTest {

@Mock
private ReviewRepository reviewRepository;

@InjectMocks
private ReviewServiceImpl reviewService;

private Review testReview;
private ReviewDto testReviewDto;

@BeforeEach
void setUp() {
testReview = new Review();
testReview.setId(1L);
            testReview.setBookId(100L);
            testReview.setUserId(100L);
            testReview.setRating(100);
            testReview.setComment("testComment");
            testReview.setReviewDate(java.time.LocalDate.now());

testReviewDto = new ReviewDto();
testReviewDto.setId(1L);
            testReviewDto.setBookId(100L);
            testReviewDto.setUserId(100L);
            testReviewDto.setRating(100);
            testReviewDto.setComment("testComment");
            testReviewDto.setReviewDate(java.time.LocalDate.now());
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Review> entities = Arrays.asList(testReview);
when(reviewRepository.findAll()).thenReturn(entities);

// When
List<ReviewDto> result = reviewService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testReview.getId());
    verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<ReviewDto> result = reviewService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(reviewRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(testReview));

        // When
        Optional<ReviewDto> result = reviewService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(reviewRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<ReviewDto> result = reviewService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(reviewRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

                // When
                ReviewDto result = reviewService.save(testReviewDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testReview.getId());
                verify(reviewRepository).save(any(Review.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(testReview));
                when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

                // When
                Optional<ReviewDto> result = reviewService.update(1L, testReviewDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(reviewRepository).findById(1L);
                    verify(reviewRepository).save(any(Review.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<ReviewDto> result = reviewService.update(999L, testReviewDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(reviewRepository).findById(999L);
                        verify(reviewRepository, never()).save(any(Review.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(reviewRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = reviewService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(reviewRepository).existsById(1L);
                        verify(reviewRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(reviewRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = reviewService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(reviewRepository).existsById(999L);
                        verify(reviewRepository, never()).deleteById(anyLong());
                        }
                        }