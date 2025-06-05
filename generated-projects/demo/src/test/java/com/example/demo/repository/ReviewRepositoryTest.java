package com.example.demo.repository;

import com.example.demo.entity.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Review Repository Tests")
class ReviewRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private ReviewRepository reviewRepository;

private Review testReview;

@BeforeEach
void setUp() {
testReview = new Review();
            testReview.setBookId(100L);
            testReview.setUserId(100L);
            testReview.setRating(100);
            testReview.setComment("testComment");
            testReview.setReviewDate(java.time.LocalDate.now());
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Review savedEntity = entityManager.persistAndFlush(testReview);

// When
Optional<Review> result = reviewRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getBookId()).isEqualTo(testReview.getBookId());
        assertThat(result.get().getUserId()).isEqualTo(testReview.getUserId());
        assertThat(result.get().getRating()).isEqualTo(testReview.getRating());
        assertThat(result.get().getComment()).isEqualTo(testReview.getComment());
        assertThat(result.get().getReviewDate()).isEqualTo(testReview.getReviewDate());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Review> result = reviewRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Review savedEntity = reviewRepository.save(testReview);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getBookId()).isEqualTo(testReview.getBookId());
        assertThat(savedEntity.getUserId()).isEqualTo(testReview.getUserId());
        assertThat(savedEntity.getRating()).isEqualTo(testReview.getRating());
        assertThat(savedEntity.getComment()).isEqualTo(testReview.getComment());
        assertThat(savedEntity.getReviewDate()).isEqualTo(testReview.getReviewDate());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Review savedEntity = entityManager.persistAndFlush(testReview);
entityManager.detach(savedEntity);

            savedEntity.setBookId(200L);
            savedEntity.setUserId(200L);
            savedEntity.setRating(200);
            savedEntity.setComment("updatedComment");
            savedEntity.setReviewDate(java.time.LocalDate.now().plusDays(1));

// When
Review updatedEntity = reviewRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getBookId()).isEqualTo(savedEntity.getBookId());
        assertThat(updatedEntity.getUserId()).isEqualTo(savedEntity.getUserId());
        assertThat(updatedEntity.getRating()).isEqualTo(savedEntity.getRating());
        assertThat(updatedEntity.getComment()).isEqualTo(savedEntity.getComment());
        assertThat(updatedEntity.getReviewDate()).isEqualTo(savedEntity.getReviewDate());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Review savedEntity = entityManager.persistAndFlush(testReview);

// When
reviewRepository.deleteById(savedEntity.getId());

// Then
Optional<Review> result = reviewRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Review entity1 = entityManager.persistAndFlush(testReview);

Review entity2 = new Review();
            entity2.setBookId(200L);
            entity2.setUserId(200L);
            entity2.setRating(200);
            entity2.setComment("anotherComment");
            entity2.setReviewDate(java.time.LocalDate.now().plusDays(1));
entityManager.persistAndFlush(entity2);

// When
List<Review> entities = reviewRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Review entity = new Review();
            entity.setBookId(100L + i);
            entity.setUserId(100L + i);
            entity.setRating(100 + i);
            entity.setComment("testComment" + i);
            entity.setReviewDate(java.time.LocalDate.now().plusDays(i));
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Review> page = reviewRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Review savedEntity = entityManager.persistAndFlush(testReview);

// When
boolean exists = reviewRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = reviewRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testReview);

Review entity2 = new Review();
            entity2.setBookId(200L);
            entity2.setUserId(200L);
            entity2.setRating(200);
            entity2.setComment("anotherComment");
            entity2.setReviewDate(java.time.LocalDate.now().plusDays(1));
entityManager.persistAndFlush(entity2);

// When
long count = reviewRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}