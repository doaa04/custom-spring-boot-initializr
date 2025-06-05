package com.example.demo.repository;

import com.example.demo.entity.Doctor;
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
@DisplayName("Doctor Repository Tests")
class DoctorRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private DoctorRepository doctorRepository;

private Doctor testDoctor;

@BeforeEach
void setUp() {
testDoctor = new Doctor();
            testDoctor.setFirstName("testFirstName");
            testDoctor.setLastName("testLastName");
            testDoctor.setSpecialization("testSpecialization");
            testDoctor.setEmail("testEmail");
            testDoctor.setPhoneNumber("testPhoneNumber");
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Doctor savedEntity = entityManager.persistAndFlush(testDoctor);

// When
Optional<Doctor> result = doctorRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getFirstName()).isEqualTo(testDoctor.getFirstName());
        assertThat(result.get().getLastName()).isEqualTo(testDoctor.getLastName());
        assertThat(result.get().getSpecialization()).isEqualTo(testDoctor.getSpecialization());
        assertThat(result.get().getEmail()).isEqualTo(testDoctor.getEmail());
        assertThat(result.get().getPhoneNumber()).isEqualTo(testDoctor.getPhoneNumber());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Doctor> result = doctorRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Doctor savedEntity = doctorRepository.save(testDoctor);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo(testDoctor.getFirstName());
        assertThat(savedEntity.getLastName()).isEqualTo(testDoctor.getLastName());
        assertThat(savedEntity.getSpecialization()).isEqualTo(testDoctor.getSpecialization());
        assertThat(savedEntity.getEmail()).isEqualTo(testDoctor.getEmail());
        assertThat(savedEntity.getPhoneNumber()).isEqualTo(testDoctor.getPhoneNumber());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Doctor savedEntity = entityManager.persistAndFlush(testDoctor);
entityManager.detach(savedEntity);

            savedEntity.setFirstName("updatedFirstName");
            savedEntity.setLastName("updatedLastName");
            savedEntity.setSpecialization("updatedSpecialization");
            savedEntity.setEmail("updatedEmail");
            savedEntity.setPhoneNumber("updatedPhoneNumber");

// When
Doctor updatedEntity = doctorRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getFirstName()).isEqualTo(savedEntity.getFirstName());
        assertThat(updatedEntity.getLastName()).isEqualTo(savedEntity.getLastName());
        assertThat(updatedEntity.getSpecialization()).isEqualTo(savedEntity.getSpecialization());
        assertThat(updatedEntity.getEmail()).isEqualTo(savedEntity.getEmail());
        assertThat(updatedEntity.getPhoneNumber()).isEqualTo(savedEntity.getPhoneNumber());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Doctor savedEntity = entityManager.persistAndFlush(testDoctor);

// When
doctorRepository.deleteById(savedEntity.getId());

// Then
Optional<Doctor> result = doctorRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Doctor entity1 = entityManager.persistAndFlush(testDoctor);

Doctor entity2 = new Doctor();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setSpecialization("anotherSpecialization");
            entity2.setEmail("anotherEmail");
            entity2.setPhoneNumber("anotherPhoneNumber");
entityManager.persistAndFlush(entity2);

// When
List<Doctor> entities = doctorRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Doctor entity = new Doctor();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setSpecialization("testSpecialization" + i);
            entity.setEmail("testEmail" + i);
            entity.setPhoneNumber("testPhoneNumber" + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Doctor> page = doctorRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Doctor savedEntity = entityManager.persistAndFlush(testDoctor);

// When
boolean exists = doctorRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = doctorRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testDoctor);

Doctor entity2 = new Doctor();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setSpecialization("anotherSpecialization");
            entity2.setEmail("anotherEmail");
            entity2.setPhoneNumber("anotherPhoneNumber");
entityManager.persistAndFlush(entity2);

// When
long count = doctorRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}