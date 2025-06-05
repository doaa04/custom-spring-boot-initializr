package com.example.demo.controller;

import com.example.demo.dto.PrescriptionDto;
import com.example.demo.entity.Prescription;
import com.example.demo.repository.PrescriptionRepository;
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
@DisplayName("Prescription Controller Integration Tests")
class PrescriptionControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private PrescriptionRepository prescriptionRepository;

private PrescriptionDto testPrescriptionDto;
private Prescription savedPrescription;

@BeforeEach
void setUp() {
prescriptionRepository.deleteAll();

testPrescriptionDto = new PrescriptionDto();
            testPrescriptionDto.setDateIssued(java.time.LocalDate.now());
            testPrescriptionDto.setMedicationDetails("testMedicationDetails");
            testPrescriptionDto.setDosageInstructions("testDosageInstructions");
            testPrescriptionDto.setPatientId(100L);
            testPrescriptionDto.setDoctorId(100L);

// Create a saved entity for tests
Prescription entity = new Prescription();
            entity.setDateIssued(java.time.LocalDate.now().minusDays(1));
            entity.setMedicationDetails("existingMedicationDetails");
            entity.setDosageInstructions("existingDosageInstructions");
            entity.setPatientId(200L);
            entity.setDoctorId(200L);
savedPrescription = prescriptionRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/prescriptions")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/prescriptions"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedPrescription.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/prescriptions/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/prescriptions/{id}", savedPrescription.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedPrescription.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/prescriptions/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/prescriptions/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/prescriptions with valid data")
void createPrescription_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/prescriptions")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testPrescriptionDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/prescriptions with invalid data")
void createPrescription_InvalidInput_ReturnsBadRequest() throws Exception {
PrescriptionDto invalidDto = new PrescriptionDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/prescriptions")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/prescriptions/{id} with valid data")
void updatePrescription_ValidInput_ReturnsUpdated() throws Exception {
testPrescriptionDto.setId(savedPrescription.getId());

mockMvc.perform(put("/api/prescriptions/{id}", savedPrescription.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testPrescriptionDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedPrescription.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/prescriptions/{id} with non-existing ID")
void updatePrescription_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/prescriptions/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testPrescriptionDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/prescriptions/{id} with mismatched IDs")
void updatePrescription_MismatchedIds_ReturnsBadRequest() throws Exception {
testPrescriptionDto.setId(999L);

mockMvc.perform(put("/api/prescriptions/{id}", savedPrescription.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testPrescriptionDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/prescriptions/{id} with existing ID")
void deletePrescription_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/prescriptions/{id}", savedPrescription.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/prescriptions/{id}", savedPrescription.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/prescriptions/{id} with non-existing ID")
void deletePrescription_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/prescriptions/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/prescriptions with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Prescription entity = new Prescription();
            entity.setDateIssued(java.time.LocalDate.now().minusDays(i));
            entity.setMedicationDetails("testMedicationDetails" + i);
            entity.setDosageInstructions("testDosageInstructions" + i);
            entity.setPatientId(100L + i);
            entity.setDoctorId(100L + i);
prescriptionRepository.save(entity);
}

mockMvc.perform(get("/api/prescriptions")
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
void createPrescription_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/prescriptions")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}