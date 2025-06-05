package com.example.demo.controller;

import com.example.demo.dto.AuthorDto;
import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
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
@DisplayName("Author Controller Integration Tests")
class AuthorControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private AuthorRepository authorRepository;

private AuthorDto testAuthorDto;
private Author savedAuthor;

@BeforeEach
void setUp() {
authorRepository.deleteAll();

testAuthorDto = new AuthorDto();
            testAuthorDto.setFirstName("testFirstName");
            testAuthorDto.setLastName("testLastName");
            testAuthorDto.setBio("testBio");

// Create a saved entity for tests
Author entity = new Author();
            entity.setFirstName("existingFirstName");
            entity.setLastName("existingLastName");
            entity.setBio("existingBio");
savedAuthor = authorRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/authors")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/authors"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedAuthor.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/authors/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedAuthor.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/authors/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/authors/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/authors with valid data")
void createAuthor_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/authors")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAuthorDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/authors with invalid data")
void createAuthor_InvalidInput_ReturnsBadRequest() throws Exception {
AuthorDto invalidDto = new AuthorDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/authors")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/authors/{id} with valid data")
void updateAuthor_ValidInput_ReturnsUpdated() throws Exception {
testAuthorDto.setId(savedAuthor.getId());

mockMvc.perform(put("/api/authors/{id}", savedAuthor.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAuthorDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedAuthor.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/authors/{id} with non-existing ID")
void updateAuthor_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/authors/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAuthorDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/authors/{id} with mismatched IDs")
void updateAuthor_MismatchedIds_ReturnsBadRequest() throws Exception {
testAuthorDto.setId(999L);

mockMvc.perform(put("/api/authors/{id}", savedAuthor.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAuthorDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/authors/{id} with existing ID")
void deleteAuthor_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/authors/{id}", savedAuthor.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/authors/{id} with non-existing ID")
void deleteAuthor_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/authors/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/authors with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Author entity = new Author();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setBio("testBio" + i);
authorRepository.save(entity);
}

mockMvc.perform(get("/api/authors")
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
void createAuthor_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/authors")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}