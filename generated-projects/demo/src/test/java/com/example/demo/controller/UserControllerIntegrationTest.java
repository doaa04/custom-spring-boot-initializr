package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
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
@DisplayName("User Controller Integration Tests")
class UserControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private UserRepository userRepository;

private UserDto testUserDto;
private User savedUser;

@BeforeEach
void setUp() {
userRepository.deleteAll();

testUserDto = new UserDto();
            testUserDto.setUsername("testUsername");
            testUserDto.setEmail("testEmail");
            testUserDto.setPasswordHash("testPasswordHash");

// Create a saved entity for tests
User entity = new User();
            entity.setUsername("existingUsername");
            entity.setEmail("existingEmail");
            entity.setPasswordHash("existingPasswordHash");
savedUser = userRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/users")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/users"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedUser.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/users/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedUser.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/users/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/users/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/users with valid data")
void createUser_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/users")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testUserDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/users with invalid data")
void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
UserDto invalidDto = new UserDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/users")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/users/{id} with valid data")
void updateUser_ValidInput_ReturnsUpdated() throws Exception {
testUserDto.setId(savedUser.getId());

mockMvc.perform(put("/api/users/{id}", savedUser.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testUserDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedUser.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/users/{id} with non-existing ID")
void updateUser_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/users/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testUserDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/users/{id} with mismatched IDs")
void updateUser_MismatchedIds_ReturnsBadRequest() throws Exception {
testUserDto.setId(999L);

mockMvc.perform(put("/api/users/{id}", savedUser.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testUserDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/users/{id} with existing ID")
void deleteUser_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/users/{id}", savedUser.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/users/{id}", savedUser.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/users/{id} with non-existing ID")
void deleteUser_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/users/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/users with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
User entity = new User();
            entity.setUsername("testUsername" + i);
            entity.setEmail("testEmail" + i);
            entity.setPasswordHash("testPasswordHash" + i);
userRepository.save(entity);
}

mockMvc.perform(get("/api/users")
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
void createUser_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/users")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}