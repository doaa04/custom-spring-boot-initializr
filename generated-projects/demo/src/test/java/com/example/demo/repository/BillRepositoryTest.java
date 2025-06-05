package com.example.demo.repository;

import com.example.demo.entity.Bill;
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
@DisplayName("Bill Repository Tests")
class BillRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private BillRepository billRepository;

private Bill testBill;

@BeforeEach
void setUp() {
testBill = new Bill();
            testBill.setAmount(100.0);
            testBill.setIssueDate(java.time.LocalDate.now());
            testBill.setDueDate(java.time.LocalDate.now());
            testBill.setStatus("testStatus");
            testBill.setPatientId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Bill savedEntity = entityManager.persistAndFlush(testBill);

// When
Optional<Bill> result = billRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getAmount()).isEqualTo(testBill.getAmount());
        assertThat(result.get().getIssueDate()).isEqualTo(testBill.getIssueDate());
        assertThat(result.get().getDueDate()).isEqualTo(testBill.getDueDate());
        assertThat(result.get().getStatus()).isEqualTo(testBill.getStatus());
        assertThat(result.get().getPatientId()).isEqualTo(testBill.getPatientId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Bill> result = billRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Bill savedEntity = billRepository.save(testBill);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getAmount()).isEqualTo(testBill.getAmount());
        assertThat(savedEntity.getIssueDate()).isEqualTo(testBill.getIssueDate());
        assertThat(savedEntity.getDueDate()).isEqualTo(testBill.getDueDate());
        assertThat(savedEntity.getStatus()).isEqualTo(testBill.getStatus());
        assertThat(savedEntity.getPatientId()).isEqualTo(testBill.getPatientId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Bill savedEntity = entityManager.persistAndFlush(testBill);
entityManager.detach(savedEntity);

            savedEntity.setAmount(200.0);
            savedEntity.setIssueDate(java.time.LocalDate.now().plusDays(1));
            savedEntity.setDueDate(java.time.LocalDate.now().plusDays(1));
            savedEntity.setStatus("updatedStatus");
            savedEntity.setPatientId(200L);

// When
Bill updatedEntity = billRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getAmount()).isEqualTo(savedEntity.getAmount());
        assertThat(updatedEntity.getIssueDate()).isEqualTo(savedEntity.getIssueDate());
        assertThat(updatedEntity.getDueDate()).isEqualTo(savedEntity.getDueDate());
        assertThat(updatedEntity.getStatus()).isEqualTo(savedEntity.getStatus());
        assertThat(updatedEntity.getPatientId()).isEqualTo(savedEntity.getPatientId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Bill savedEntity = entityManager.persistAndFlush(testBill);

// When
billRepository.deleteById(savedEntity.getId());

// Then
Optional<Bill> result = billRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Bill entity1 = entityManager.persistAndFlush(testBill);

Bill entity2 = new Bill();
            entity2.setAmount(200.0);
            entity2.setIssueDate(java.time.LocalDate.now().plusDays(1));
            entity2.setDueDate(java.time.LocalDate.now().plusDays(1));
            entity2.setStatus("anotherStatus");
            entity2.setPatientId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Bill> entities = billRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Bill entity = new Bill();
            entity.setAmount(100.0 + i);
            entity.setIssueDate(java.time.LocalDate.now().plusDays(i));
            entity.setDueDate(java.time.LocalDate.now().plusDays(i));
            entity.setStatus("testStatus" + i);
            entity.setPatientId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Bill> page = billRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Bill savedEntity = entityManager.persistAndFlush(testBill);

// When
boolean exists = billRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = billRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testBill);

Bill entity2 = new Bill();
            entity2.setAmount(200.0);
            entity2.setIssueDate(java.time.LocalDate.now().plusDays(1));
            entity2.setDueDate(java.time.LocalDate.now().plusDays(1));
            entity2.setStatus("anotherStatus");
            entity2.setPatientId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = billRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}