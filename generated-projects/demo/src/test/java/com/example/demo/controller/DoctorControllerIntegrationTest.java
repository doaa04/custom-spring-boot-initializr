package com.example.demo.controller;

import com.example.demo.dto.DoctorDto;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.DoctorRepository;
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
@DisplayName("Doctor Controller Integration Tests")
class DoctorControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private DoctorRepository doctorRepository;

private DoctorDto testDoctorDto;
private Doctor savedDoctor;

@BeforeEach
void setUp() {
doctorRepository.deleteAll();

testDoctorDto = new DoctorDto();
            testDoctorDto.setFirstName("testFirstName");
            testDoctorDto.setLastName("testLastName");
            testDoctorDto.setSpecialization("testSpecialization");
            testDoctorDto.setEmail("testEmail");
            testDoctorDto.setPhoneNumber("testPhoneNumber");

// Create a saved entity for tests
Doctor entity = new Doctor();
            entity.setFirstName("existingFirstName");
            entity.setLastName("existingLastName");
            entity.setSpecialization("existingSpecialization");
            entity.setEmail("existingEmail");
            entity.setPhoneNumber("existingPhoneNumber");
savedDoctor = doctorRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/doctors")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/doctors"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedDoctor.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/doctors/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/doctors/{id}", savedDoctor.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedDoctor.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/doctors/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/doctors/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/doctors with valid data")
void createDoctor_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/doctors")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDoctorDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/doctors with invalid data")
void createDoctor_InvalidInput_ReturnsBadRequest() throws Exception {
DoctorDto invalidDto = new DoctorDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/doctors")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/doctors/{id} with valid data")
void updateDoctor_ValidInput_ReturnsUpdated() throws Exception {
testDoctorDto.setId(savedDoctor.getId());

mockMvc.perform(put("/api/doctors/{id}", savedDoctor.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDoctorDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedDoctor.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/doctors/{id} with non-existing ID")
void updateDoctor_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/doctors/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDoctorDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/doctors/{id} with mismatched IDs")
void updateDoctor_MismatchedIds_ReturnsBadRequest() throws Exception {
testDoctorDto.setId(999L);

mockMvc.perform(put("/api/doctors/{id}", savedDoctor.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testDoctorDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/doctors/{id} with existing ID")
void deleteDoctor_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/doctors/{id}", savedDoctor.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/doctors/{id}", savedDoctor.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/doctors/{id} with non-existing ID")
void deleteDoctor_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/doctors/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/doctors with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Doctor entity = new Doctor();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setSpecialization("testSpecialization" + i);
            entity.setEmail("testEmail" + i);
            entity.setPhoneNumber("testPhoneNumber" + i);
doctorRepository.save(entity);
}

mockMvc.perform(get("/api/doctors")
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
void createDoctor_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/doctors")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}