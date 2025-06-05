package com.example.demo.controller;

import com.example.demo.dto.RoomDto;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
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
@DisplayName("Room Controller Integration Tests")
class RoomControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private RoomRepository roomRepository;

private RoomDto testRoomDto;
private Room savedRoom;

@BeforeEach
void setUp() {
roomRepository.deleteAll();

testRoomDto = new RoomDto();
            testRoomDto.setRoomNumber("testRoomNumber");
            testRoomDto.setType("testType");
            testRoomDto.setIsAvailable(true);
            testRoomDto.setDepartmentId(100L);

// Create a saved entity for tests
Room entity = new Room();
            entity.setRoomNumber("existingRoomNumber");
            entity.setType("existingType");
            entity.setIsAvailable(false);
            entity.setDepartmentId(200L);
savedRoom = roomRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/rooms")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/rooms"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedRoom.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/rooms/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/rooms/{id}", savedRoom.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedRoom.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/rooms/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/rooms/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/rooms with valid data")
void createRoom_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/rooms")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testRoomDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/rooms with invalid data")
void createRoom_InvalidInput_ReturnsBadRequest() throws Exception {
RoomDto invalidDto = new RoomDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/rooms")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/rooms/{id} with valid data")
void updateRoom_ValidInput_ReturnsUpdated() throws Exception {
testRoomDto.setId(savedRoom.getId());

mockMvc.perform(put("/api/rooms/{id}", savedRoom.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testRoomDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedRoom.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/rooms/{id} with non-existing ID")
void updateRoom_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/rooms/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testRoomDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/rooms/{id} with mismatched IDs")
void updateRoom_MismatchedIds_ReturnsBadRequest() throws Exception {
testRoomDto.setId(999L);

mockMvc.perform(put("/api/rooms/{id}", savedRoom.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testRoomDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/rooms/{id} with existing ID")
void deleteRoom_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/rooms/{id}", savedRoom.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/rooms/{id}", savedRoom.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/rooms/{id} with non-existing ID")
void deleteRoom_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/rooms/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/rooms with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Room entity = new Room();
            entity.setRoomNumber("testRoomNumber" + i);
            entity.setType("testType" + i);
            entity.setIsAvailable(i % 2 == 0);
            entity.setDepartmentId(100L + i);
roomRepository.save(entity);
}

mockMvc.perform(get("/api/rooms")
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
void createRoom_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/rooms")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}