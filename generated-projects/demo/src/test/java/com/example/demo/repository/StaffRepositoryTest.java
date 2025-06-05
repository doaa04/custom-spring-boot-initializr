package com.example.demo.repository;

import com.example.demo.entity.Staff;
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
@DisplayName("Staff Repository Tests")
class StaffRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private StaffRepository staffRepository;

private Staff testStaff;

@BeforeEach
void setUp() {
testStaff = new Staff();
            testStaff.setFirstName("testFirstName");
            testStaff.setLastName("testLastName");
            testStaff.setRole("testRole");
            testStaff.setEmail("testEmail");
            testStaff.setPhoneNumber("testPhoneNumber");
            testStaff.setDepartmentId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Staff savedEntity = entityManager.persistAndFlush(testStaff);

// When
Optional<Staff> result = staffRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getFirstName()).isEqualTo(testStaff.getFirstName());
        assertThat(result.get().getLastName()).isEqualTo(testStaff.getLastName());
        assertThat(result.get().getRole()).isEqualTo(testStaff.getRole());
        assertThat(result.get().getEmail()).isEqualTo(testStaff.getEmail());
        assertThat(result.get().getPhoneNumber()).isEqualTo(testStaff.getPhoneNumber());
        assertThat(result.get().getDepartmentId()).isEqualTo(testStaff.getDepartmentId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Staff> result = staffRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Staff savedEntity = staffRepository.save(testStaff);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo(testStaff.getFirstName());
        assertThat(savedEntity.getLastName()).isEqualTo(testStaff.getLastName());
        assertThat(savedEntity.getRole()).isEqualTo(testStaff.getRole());
        assertThat(savedEntity.getEmail()).isEqualTo(testStaff.getEmail());
        assertThat(savedEntity.getPhoneNumber()).isEqualTo(testStaff.getPhoneNumber());
        assertThat(savedEntity.getDepartmentId()).isEqualTo(testStaff.getDepartmentId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Staff savedEntity = entityManager.persistAndFlush(testStaff);
entityManager.detach(savedEntity);

            savedEntity.setFirstName("updatedFirstName");
            savedEntity.setLastName("updatedLastName");
            savedEntity.setRole("updatedRole");
            savedEntity.setEmail("updatedEmail");
            savedEntity.setPhoneNumber("updatedPhoneNumber");
            savedEntity.setDepartmentId(200L);

// When
Staff updatedEntity = staffRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getFirstName()).isEqualTo(savedEntity.getFirstName());
        assertThat(updatedEntity.getLastName()).isEqualTo(savedEntity.getLastName());
        assertThat(updatedEntity.getRole()).isEqualTo(savedEntity.getRole());
        assertThat(updatedEntity.getEmail()).isEqualTo(savedEntity.getEmail());
        assertThat(updatedEntity.getPhoneNumber()).isEqualTo(savedEntity.getPhoneNumber());
        assertThat(updatedEntity.getDepartmentId()).isEqualTo(savedEntity.getDepartmentId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Staff savedEntity = entityManager.persistAndFlush(testStaff);

// When
staffRepository.deleteById(savedEntity.getId());

// Then
Optional<Staff> result = staffRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Staff entity1 = entityManager.persistAndFlush(testStaff);

Staff entity2 = new Staff();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setRole("anotherRole");
            entity2.setEmail("anotherEmail");
            entity2.setPhoneNumber("anotherPhoneNumber");
            entity2.setDepartmentId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Staff> entities = staffRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Staff entity = new Staff();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setRole("testRole" + i);
            entity.setEmail("testEmail" + i);
            entity.setPhoneNumber("testPhoneNumber" + i);
            entity.setDepartmentId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Staff> page = staffRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Staff savedEntity = entityManager.persistAndFlush(testStaff);

// When
boolean exists = staffRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = staffRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testStaff);

Staff entity2 = new Staff();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setRole("anotherRole");
            entity2.setEmail("anotherEmail");
            entity2.setPhoneNumber("anotherPhoneNumber");
            entity2.setDepartmentId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = staffRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}