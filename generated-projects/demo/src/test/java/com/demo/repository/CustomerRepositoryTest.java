package com.demo.repository;

import com.demo.entity.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Customer Repository Tests")
class CustomerRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private CustomerRepository customerRepository;

private Customer testCustomer;

@BeforeEach
void setUp() {
testCustomer = new Customer();
            testCustomer.setName("testName");
            testCustomer.setBudget(100);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Customer savedEntity = entityManager.persistAndFlush(testCustomer);

// When
Optional<Customer> result = customerRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo(testCustomer.getName());
        assertThat(result.get().getBudget()).isEqualTo(testCustomer.getBudget());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Customer> result = customerRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Customer savedEntity = customerRepository.save(testCustomer);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getName()).isEqualTo(testCustomer.getName());
        assertThat(savedEntity.getBudget()).isEqualTo(testCustomer.getBudget());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Customer savedEntity = entityManager.persistAndFlush(testCustomer);
entityManager.detach(savedEntity);

            savedEntity.setName("updatedName");
            savedEntity.setBudget(200);

// When
Customer updatedEntity = customerRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getName()).isEqualTo(savedEntity.getName());
        assertThat(updatedEntity.getBudget()).isEqualTo(savedEntity.getBudget());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Customer savedEntity = entityManager.persistAndFlush(testCustomer);

// When
customerRepository.deleteById(savedEntity.getId());

// Then
Optional<Customer> result = customerRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Customer entity1 = entityManager.persistAndFlush(testCustomer);

Customer entity2 = new Customer();
            entity2.setName("anotherName");
            entity2.setBudget(200);
entityManager.persistAndFlush(entity2);

// When
List<Customer> entities = customerRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Customer entity = new Customer();
            entity.setName("testName" + i);
            entity.setBudget(100 + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Customer> page = customerRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Customer savedEntity = entityManager.persistAndFlush(testCustomer);

// When
boolean exists = customerRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = customerRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testCustomer);

Customer entity2 = new Customer();
            entity2.setName("anotherName");
            entity2.setBudget(200);
entityManager.persistAndFlush(entity2);

// When
long count = customerRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}