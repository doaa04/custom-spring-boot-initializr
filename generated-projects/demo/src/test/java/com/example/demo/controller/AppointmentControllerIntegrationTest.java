package com.example.demo.controller;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.entity.Appointment;
import com.example.demo.repository.AppointmentRepository;
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
@DisplayName("Appointment Controller Integration Tests")
class AppointmentControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private AppointmentRepository appointmentRepository;

private AppointmentDto testAppointmentDto;
private Appointment savedAppointment;

@BeforeEach
void setUp() {
appointmentRepository.deleteAll();

testAppointmentDto = new AppointmentDto();
            testAppointmentDto.setAppointmentDate(java.time.LocalDate.now());
            testAppointmentDto.setAppointmentTime("testAppointmentTime");
            testAppointmentDto.setReason("testReason");
            testAppointmentDto.setStatus("testStatus");
            testAppointmentDto.setPatientId(100L);
            testAppointmentDto.setDoctorId(100L);

// Create a saved entity for tests
Appointment entity = new Appointment();
            entity.setAppointmentDate(java.time.LocalDate.now().minusDays(1));
            entity.setAppointmentTime("existingAppointmentTime");
            entity.setReason("existingReason");
            entity.setStatus("existingStatus");
            entity.setPatientId(200L);
            entity.setDoctorId(200L);
savedAppointment = appointmentRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/appointments")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/appointments"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedAppointment.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/appointments/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/appointments/{id}", savedAppointment.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedAppointment.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/appointments/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/appointments/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/appointments with valid data")
void createAppointment_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/appointments")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAppointmentDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/appointments with invalid data")
void createAppointment_InvalidInput_ReturnsBadRequest() throws Exception {
AppointmentDto invalidDto = new AppointmentDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/appointments")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/appointments/{id} with valid data")
void updateAppointment_ValidInput_ReturnsUpdated() throws Exception {
testAppointmentDto.setId(savedAppointment.getId());

mockMvc.perform(put("/api/appointments/{id}", savedAppointment.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAppointmentDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedAppointment.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/appointments/{id} with non-existing ID")
void updateAppointment_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/appointments/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAppointmentDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/appointments/{id} with mismatched IDs")
void updateAppointment_MismatchedIds_ReturnsBadRequest() throws Exception {
testAppointmentDto.setId(999L);

mockMvc.perform(put("/api/appointments/{id}", savedAppointment.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testAppointmentDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/appointments/{id} with existing ID")
void deleteAppointment_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/appointments/{id}", savedAppointment.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/appointments/{id}", savedAppointment.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/appointments/{id} with non-existing ID")
void deleteAppointment_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/appointments/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/appointments with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Appointment entity = new Appointment();
            entity.setAppointmentDate(java.time.LocalDate.now().minusDays(i));
            entity.setAppointmentTime("testAppointmentTime" + i);
            entity.setReason("testReason" + i);
            entity.setStatus("testStatus" + i);
            entity.setPatientId(100L + i);
            entity.setDoctorId(100L + i);
appointmentRepository.save(entity);
}

mockMvc.perform(get("/api/appointments")
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
void createAppointment_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/appointments")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}