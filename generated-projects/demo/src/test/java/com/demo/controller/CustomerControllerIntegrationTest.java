package com.demo.controller;

import com.demo.dto.CustomerDto;
import com.demo.entity.Customer;
import com.demo.repository.CustomerRepository;
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
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

@Autowired
private MockMvc mockMvc;

@Autowired
private ObjectMapper objectMapper;

@Autowired
private CustomerRepository customerRepository;

private CustomerDto testCustomerDto;
private Customer savedCustomer;

@BeforeEach
void setUp() {
customerRepository.deleteAll();

testCustomerDto = new CustomerDto();
            testCustomerDto.setName("testName");
            testCustomerDto.setBudget(100);

// Create a saved entity for tests
Customer entity = new Customer();
            entity.setName("existingName");
            entity.setBudget(200);
savedCustomer = customerRepository.save(entity);
}

@Test
@DisplayName("Should return all entities when GET /api/customers")
void getAllEntities_ReturnsListOfEntities() throws Exception {
mockMvc.perform(get("/api/customers"))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.content", hasSize(1)))
.andExpect(jsonPath("$.content[0].id", is(savedCustomer.getId().intValue())))
.andExpect(jsonPath("$.totalElements", is(1)))
.andExpect(jsonPath("$.totalPages", is(1)));
}

@Test
@DisplayName("Should return entity when GET /api/customers/{id} with existing ID")
void getEntityById_ExistingId_ReturnsEntity() throws Exception {
mockMvc.perform(get("/api/customers/{id}", savedCustomer.getId()))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when GET /api/customers/{id} with non-existing ID")
void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(get("/api/customers/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should create entity when POST /api/customers with valid data")
void createCustomer_ValidInput_ReturnsCreated() throws Exception {
mockMvc.perform(post("/api/customers")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testCustomerDto)))
.andExpect(status().isCreated())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", notNullValue()));
}

@Test
@DisplayName("Should return 400 when POST /api/customers with invalid data")
void createCustomer_InvalidInput_ReturnsBadRequest() throws Exception {
CustomerDto invalidDto = new CustomerDto();
// Leave required fields empty to trigger validation errors

mockMvc.perform(post("/api/customers")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(invalidDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should update entity when PUT /api/customers/{id} with valid data")
void updateCustomer_ValidInput_ReturnsUpdated() throws Exception {
testCustomerDto.setId(savedCustomer.getId());

mockMvc.perform(put("/api/customers/{id}", savedCustomer.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testCustomerDto)))
.andExpect(status().isOk())
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())));
}

@Test
@DisplayName("Should return 404 when PUT /api/customers/{id} with non-existing ID")
void updateCustomer_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(put("/api/customers/999")
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testCustomerDto)))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 400 when PUT /api/customers/{id} with mismatched IDs")
void updateCustomer_MismatchedIds_ReturnsBadRequest() throws Exception {
testCustomerDto.setId(999L);

mockMvc.perform(put("/api/customers/{id}", savedCustomer.getId())
.contentType(MediaType.APPLICATION_JSON)
.content(objectMapper.writeValueAsString(testCustomerDto)))
.andExpect(status().isBadRequest());
}

@Test
@DisplayName("Should delete entity when DELETE /api/customers/{id} with existing ID")
void deleteCustomer_ExistingId_ReturnsNoContent() throws Exception {
mockMvc.perform(delete("/api/customers/{id}", savedCustomer.getId()))
.andExpect(status().isNoContent());

// Verify entity is deleted
mockMvc.perform(get("/api/customers/{id}", savedCustomer.getId()))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should return 404 when DELETE /api/customers/{id} with non-existing ID")
void deleteCustomer_NonExistingId_ReturnsNotFound() throws Exception {
mockMvc.perform(delete("/api/customers/999"))
.andExpect(status().isNotFound());
}

@Test
@DisplayName("Should handle pagination when GET /api/customers with page parameters")
void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
// Create additional entities for pagination test
for (int i = 0; i < 5; i++) {
Customer entity = new Customer();
            entity.setName("testName" + i);
            entity.setBudget(100 + i);
customerRepository.save(entity);
}

mockMvc.perform(get("/api/customers")
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
void createCustomer_MalformedJson_ReturnsBadRequest() throws Exception {
mockMvc.perform(post("/api/customers")
.contentType(MediaType.APPLICATION_JSON)
.content("{invalid json}"))
.andExpect(status().isBadRequest());
}
}