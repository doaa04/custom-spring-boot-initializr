package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
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
@DisplayName("Department Controller Integration Tests")
class DepartmentControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private DepartmentRepository departmentRepository;

private DepartmentDto testDepartmentDto;
private Department savedDepartment;

@BeforeEach
void setUp() {
departmentRepository.deleteAll();

testDepartmentDto = new DepartmentDto();
            testDepartmentDto.setName("testName");
            testDepartmentDto.setLocation("testLocation");

// Create a saved entity for tests
Department entity = new Department();
            entity.setName("existingName");
            entity.setLocation("existingLocation");
savedDepartment = departmentRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/departments")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/departments"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedDepartment.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/departments/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/departments/{id}", savedDepartment.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedDepartment.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/departments/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/departments/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/departments with valid data")
void createDepartment_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/departments")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDepartmentDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/departments with invalid data")
void createDepartment_InvalidInput_ReturnsBadRequest() throws Exception {
DepartmentDto invalidDto = new DepartmentDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/departments")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/departments/{id} with valid data")
void updateDepartment_ValidInput_ReturnsUpdated() throws Exception {
testDepartmentDto.setId(savedDepartment.getId());

mockMvc.perform(put("/api/departments/{id}", savedDepartment.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDepartmentDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedDepartment.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/departments/{id} with non-existing ID")
void updateDepartment_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/departments/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDepartmentDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/departments/{id} with mismatched IDs")
void updateDepartment_MismatchedIds_ReturnsBadRequest() throws Exception {
testDepartmentDto.setId(999L);

mockMvc.perform(put("/api/departments/{id}", savedDepartment.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDepartmentDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/departments/{id} with existing ID")
void deleteDepartment_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/departments/{id}", savedDepartment.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/departments/{id}", savedDepartment.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/departments/{id} with non-existing ID")
void deleteDepartment_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/departments/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/departments with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Department entity = new Department();
            entity.setName("testName" + i);
            entity.setLocation("testLocation" + i);
departmentRepository.save(entity);
}

mockMvc.perform(get("/api/departments")
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
void createDepartment_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/departments")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}