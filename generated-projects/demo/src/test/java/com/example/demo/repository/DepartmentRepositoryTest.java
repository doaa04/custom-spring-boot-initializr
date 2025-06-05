package com.example.demo.repository;

import com.example.demo.entity.Department;
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
@DisplayName("Department Repository Tests")
class DepartmentRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private DepartmentRepository departmentRepository;

private Department testDepartment;

@BeforeEach
void setUp() {
testDepartment = new Department();
            testDepartment.setName("testName");
            testDepartment.setLocation("testLocation");
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Department savedEntity = entityManager.persistAndFlush(testDepartment);

// When
Optional<Department> result = departmentRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo(testDepartment.getName());
        assertThat(result.get().getLocation()).isEqualTo(testDepartment.getLocation());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Department> result = departmentRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Department savedEntity = departmentRepository.save(testDepartment);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getName()).isEqualTo(testDepartment.getName());
        assertThat(savedEntity.getLocation()).isEqualTo(testDepartment.getLocation());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Department savedEntity = entityManager.persistAndFlush(testDepartment);
entityManager.detach(savedEntity);

            savedEntity.setName("updatedName");
            savedEntity.setLocation("updatedLocation");

// When
Department updatedEntity = departmentRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getName()).isEqualTo(savedEntity.getName());
        assertThat(updatedEntity.getLocation()).isEqualTo(savedEntity.getLocation());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Department savedEntity = entityManager.persistAndFlush(testDepartment);

// When
departmentRepository.deleteById(savedEntity.getId());

// Then
Optional<Department> result = departmentRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Department entity1 = entityManager.persistAndFlush(testDepartment);

Department entity2 = new Department();
            entity2.setName("anotherName");
            entity2.setLocation("anotherLocation");
entityManager.persistAndFlush(entity2);

// When
List<Department> entities = departmentRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Department entity = new Department();
            entity.setName("testName" + i);
            entity.setLocation("testLocation" + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Department> page = departmentRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Department savedEntity = entityManager.persistAndFlush(testDepartment);

// When
boolean exists = departmentRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = departmentRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testDepartment);

Department entity2 = new Department();
            entity2.setName("anotherName");
            entity2.setLocation("anotherLocation");
entityManager.persistAndFlush(entity2);

// When
long count = departmentRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}