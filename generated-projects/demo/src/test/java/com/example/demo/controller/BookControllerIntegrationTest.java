package com.example.demo.controller;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
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
@DisplayName("Book Controller Integration Tests")
class BookControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private BookRepository bookRepository;

private BookDto testBookDto;
private Book savedBook;

@BeforeEach
void setUp() {
bookRepository.deleteAll();

testBookDto = new BookDto();
            testBookDto.setTitle("testTitle");
            testBookDto.setIsbn("testIsbn");
            testBookDto.setPublicationDate(java.time.LocalDate.now());
            testBookDto.setPrice(100.0);
            testBookDto.setAuthorId(100L);

// Create a saved entity for tests
Book entity = new Book();
            entity.setTitle("existingTitle");
            entity.setIsbn("existingIsbn");
            entity.setPublicationDate(java.time.LocalDate.now().minusDays(1));
            entity.setPrice(200.0);
            entity.setAuthorId(200L);
savedBook = bookRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/books")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/books"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedBook.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/books/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/books/{id}", savedBook.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedBook.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/books/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/books/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/books with valid data")
void createBook_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/books")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBookDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/books with invalid data")
void createBook_InvalidInput_ReturnsBadRequest() throws Exception {
BookDto invalidDto = new BookDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/books")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/books/{id} with valid data")
void updateBook_ValidInput_ReturnsUpdated() throws Exception {
testBookDto.setId(savedBook.getId());

mockMvc.perform(put("/api/books/{id}", savedBook.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBookDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedBook.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/books/{id} with non-existing ID")
void updateBook_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/books/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBookDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/books/{id} with mismatched IDs")
void updateBook_MismatchedIds_ReturnsBadRequest() throws Exception {
testBookDto.setId(999L);

mockMvc.perform(put("/api/books/{id}", savedBook.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBookDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/books/{id} with existing ID")
void deleteBook_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/books/{id}", savedBook.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/books/{id}", savedBook.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/books/{id} with non-existing ID")
void deleteBook_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/books/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/books with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Book entity = new Book();
            entity.setTitle("testTitle" + i);
            entity.setIsbn("testIsbn" + i);
            entity.setPublicationDate(java.time.LocalDate.now().minusDays(i));
            entity.setPrice(100.0 + i);
            entity.setAuthorId(100L + i);
bookRepository.save(entity);
}

mockMvc.perform(get("/api/books")
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
void createBook_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/books")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}