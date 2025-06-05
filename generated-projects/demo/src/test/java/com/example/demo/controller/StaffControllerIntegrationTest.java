package com.example.demo.controller;

import com.example.demo.dto.StaffDto;
import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
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
@DisplayName("Staff Controller Integration Tests")
class StaffControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private StaffRepository staffRepository;

private StaffDto testStaffDto;
private Staff savedStaff;

@BeforeEach
void setUp() {
staffRepository.deleteAll();

testStaffDto = new StaffDto();
            testStaffDto.setFirstName("testFirstName");
            testStaffDto.setLastName("testLastName");
            testStaffDto.setRole("testRole");
            testStaffDto.setEmail("testEmail");
            testStaffDto.setPhoneNumber("testPhoneNumber");
            testStaffDto.setDepartmentId(100L);

// Create a saved entity for tests
Staff entity = new Staff();
            entity.setFirstName("existingFirstName");
            entity.setLastName("existingLastName");
            entity.setRole("existingRole");
            entity.setEmail("existingEmail");
            entity.setPhoneNumber("existingPhoneNumber");
            entity.setDepartmentId(200L);
savedStaff = staffRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/staff")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/staff"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedStaff.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/staff/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/staff/{id}", savedStaff.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedStaff.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/staff/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/staff/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/staff with valid data")
void createStaff_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/staff")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testStaffDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/staff with invalid data")
void createStaff_InvalidInput_ReturnsBadRequest() throws Exception {
StaffDto invalidDto = new StaffDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/staff")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/staff/{id} with valid data")
void updateStaff_ValidInput_ReturnsUpdated() throws Exception {
testStaffDto.setId(savedStaff.getId());

mockMvc.perform(put("/api/staff/{id}", savedStaff.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testStaffDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedStaff.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/staff/{id} with non-existing ID")
void updateStaff_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/staff/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testStaffDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/staff/{id} with mismatched IDs")
void updateStaff_MismatchedIds_ReturnsBadRequest() throws Exception {
testStaffDto.setId(999L);

mockMvc.perform(put("/api/staff/{id}", savedStaff.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testStaffDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/staff/{id} with existing ID")
void deleteStaff_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/staff/{id}", savedStaff.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/staff/{id}", savedStaff.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/staff/{id} with non-existing ID")
void deleteStaff_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/staff/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/staff with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Staff entity = new Staff();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setRole("testRole" + i);
            entity.setEmail("testEmail" + i);
            entity.setPhoneNumber("testPhoneNumber" + i);
            entity.setDepartmentId(100L + i);
staffRepository.save(entity);
}

mockMvc.perform(get("/api/staff")
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
void createStaff_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/staff")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}