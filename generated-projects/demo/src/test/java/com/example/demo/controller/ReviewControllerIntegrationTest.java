package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Review;
import com.example.demo.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
"spring.datasource.url=jdbc:h2:mem:testdb",
"spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("Review Controller Integration Tests")
class ReviewControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private ReviewRepository reviewRepository;

private ReviewDto testReviewDto;
private Review savedReview;

@BeforeEach
void setUp() {
reviewRepository.deleteAll();

testReviewDto = new ReviewDto();
            testReviewDto.setBookId(100L);
            testReviewDto.setUserId(100L);
            testReviewDto.setRating(100);
            testReviewDto.setComment("testComment");
            testReviewDto.setReviewDate(java.time.LocalDate.now());

// Create a saved entity for tests
Review entity = new Review();
            entity.setBookId(200L);
            entity.setUserId(200L);
            entity.setRating(200);
            entity.setComment("existingComment");
            entity.setReviewDate(java.time.LocalDate.now().minusDays(1));
savedReview = reviewRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/reviews")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/reviews"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedReview.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/reviews/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/reviews/{id}", savedReview.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedReview.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/reviews/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/reviews/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/reviews with valid data")
void createReview_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/reviews")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testReviewDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/reviews with invalid data")
void createReview_InvalidInput_ReturnsBadRequest() throws Exception {
ReviewDto invalidDto = new ReviewDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/reviews")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/reviews/{id} with valid data")
void updateReview_ValidInput_ReturnsUpdated() throws Exception {
testReviewDto.setId(savedReview.getId());

mockMvc.perform(put("/api/reviews/{id}", savedReview.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testReviewDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedReview.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/reviews/{id} with non-existing ID")
void updateReview_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/reviews/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testReviewDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/reviews/{id} with mismatched IDs")
void updateReview_MismatchedIds_ReturnsBadRequest() throws Exception {
testReviewDto.setId(999L);

mockMvc.perform(put("/api/reviews/{id}", savedReview.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testReviewDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/reviews/{id} with existing ID")
void deleteReview_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/reviews/{id}", savedReview.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/reviews/{id}", savedReview.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/reviews/{id} with non-existing ID")
void deleteReview_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/reviews/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/reviews with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Review entity = new Review();
            entity.setBookId(100L + i);
            entity.setUserId(100L + i);
            entity.setRating(100 + i);
            entity.setComment("testComment" + i);
            entity.setReviewDate(java.time.LocalDate.now().minusDays(i));
reviewRepository.save(entity);
}

mockMvc.perform(get("/api/reviews")
.param("page", "0")
.param("size", "3"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(3))))
.andExpect(jsonPath("$.size", is(3)))
.andExpect(jsonPath("$.totalElements", is(6)))
.andExpect(jsonPath("$.totalPages", is(2)));
}

@Test
@DisplayName("Should return validation errors for invalid JSON")
void createReview_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/reviews")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}