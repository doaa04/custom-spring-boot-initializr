package com.example.demo.controller;

import com.example.demo.dto.BillDto;
import com.example.demo.entity.Bill;
import com.example.demo.repository.BillRepository;
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
@DisplayName("Bill Controller Integration Tests")
class BillControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private BillRepository billRepository;

private BillDto testBillDto;
private Bill savedBill;

@BeforeEach
void setUp() {
billRepository.deleteAll();

testBillDto = new BillDto();
            testBillDto.setAmount(100.0);
            testBillDto.setIssueDate(java.time.LocalDate.now());
            testBillDto.setDueDate(java.time.LocalDate.now());
            testBillDto.setStatus("testStatus");
            testBillDto.setPatientId(100L);

// Create a saved entity for tests
Bill entity = new Bill();
            entity.setAmount(200.0);
            entity.setIssueDate(java.time.LocalDate.now().minusDays(1));
            entity.setDueDate(java.time.LocalDate.now().minusDays(1));
            entity.setStatus("existingStatus");
            entity.setPatientId(200L);
savedBill = billRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/bills")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/bills"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedBill.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/bills/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/bills/{id}", savedBill.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedBill.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/bills/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/bills/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/bills with valid data")
void createBill_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/bills")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBillDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/bills with invalid data")
void createBill_InvalidInput_ReturnsBadRequest() throws Exception {
BillDto invalidDto = new BillDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/bills")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/bills/{id} with valid data")
void updateBill_ValidInput_ReturnsUpdated() throws Exception {
testBillDto.setId(savedBill.getId());

mockMvc.perform(put("/api/bills/{id}", savedBill.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBillDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedBill.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/bills/{id} with non-existing ID")
void updateBill_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/bills/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBillDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/bills/{id} with mismatched IDs")
void updateBill_MismatchedIds_ReturnsBadRequest() throws Exception {
testBillDto.setId(999L);

mockMvc.perform(put("/api/bills/{id}", savedBill.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testBillDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/bills/{id} with existing ID")
void deleteBill_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/bills/{id}", savedBill.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/bills/{id}", savedBill.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/bills/{id} with non-existing ID")
void deleteBill_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/bills/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/bills with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Bill entity = new Bill();
            entity.setAmount(100.0 + i);
            entity.setIssueDate(java.time.LocalDate.now().minusDays(i));
            entity.setDueDate(java.time.LocalDate.now().minusDays(i));
            entity.setStatus("testStatus" + i);
            entity.setPatientId(100L + i);
billRepository.save(entity);
}

mockMvc.perform(get("/api/bills")
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
void createBill_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/bills")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}