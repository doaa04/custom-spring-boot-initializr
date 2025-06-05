package com.example.demo.repository;

import com.example.demo.entity.Patient;
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
@DisplayName("Patient Repository Tests")
class PatientRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private PatientRepository patientRepository;

private Patient testPatient;

@BeforeEach
void setUp() {
testPatient = new Patient();
            testPatient.setFirstName("testFirstName");
            testPatient.setLastName("testLastName");
            testPatient.setDateOfBirth(java.time.LocalDate.now());
            testPatient.setGender("testGender");
            testPatient.setPhoneNumber("testPhoneNumber");
            testPatient.setEmail("testEmail");
            testPatient.setAddress("testAddress");
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Patient savedEntity = entityManager.persistAndFlush(testPatient);

// When
Optional<Patient> result = patientRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getFirstName()).isEqualTo(testPatient.getFirstName());
        assertThat(result.get().getLastName()).isEqualTo(testPatient.getLastName());
        assertThat(result.get().getDateOfBirth()).isEqualTo(testPatient.getDateOfBirth());
        assertThat(result.get().getGender()).isEqualTo(testPatient.getGender());
        assertThat(result.get().getPhoneNumber()).isEqualTo(testPatient.getPhoneNumber());
        assertThat(result.get().getEmail()).isEqualTo(testPatient.getEmail());
        assertThat(result.get().getAddress()).isEqualTo(testPatient.getAddress());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Patient> result = patientRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Patient savedEntity = patientRepository.save(testPatient);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo(testPatient.getFirstName());
        assertThat(savedEntity.getLastName()).isEqualTo(testPatient.getLastName());
        assertThat(savedEntity.getDateOfBirth()).isEqualTo(testPatient.getDateOfBirth());
        assertThat(savedEntity.getGender()).isEqualTo(testPatient.getGender());
        assertThat(savedEntity.getPhoneNumber()).isEqualTo(testPatient.getPhoneNumber());
        assertThat(savedEntity.getEmail()).isEqualTo(testPatient.getEmail());
        assertThat(savedEntity.getAddress()).isEqualTo(testPatient.getAddress());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Patient savedEntity = entityManager.persistAndFlush(testPatient);
entityManager.detach(savedEntity);

            savedEntity.setFirstName("updatedFirstName");
            savedEntity.setLastName("updatedLastName");
            savedEntity.setDateOfBirth(java.time.LocalDate.now().plusDays(1));
            savedEntity.setGender("updatedGender");
            savedEntity.setPhoneNumber("updatedPhoneNumber");
            savedEntity.setEmail("updatedEmail");
            savedEntity.setAddress("updatedAddress");

// When
Patient updatedEntity = patientRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getFirstName()).isEqualTo(savedEntity.getFirstName());
        assertThat(updatedEntity.getLastName()).isEqualTo(savedEntity.getLastName());
        assertThat(updatedEntity.getDateOfBirth()).isEqualTo(savedEntity.getDateOfBirth());
        assertThat(updatedEntity.getGender()).isEqualTo(savedEntity.getGender());
        assertThat(updatedEntity.getPhoneNumber()).isEqualTo(savedEntity.getPhoneNumber());
        assertThat(updatedEntity.getEmail()).isEqualTo(savedEntity.getEmail());
        assertThat(updatedEntity.getAddress()).isEqualTo(savedEntity.getAddress());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Patient savedEntity = entityManager.persistAndFlush(testPatient);

// When
patientRepository.deleteById(savedEntity.getId());

// Then
Optional<Patient> result = patientRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Patient entity1 = entityManager.persistAndFlush(testPatient);

Patient entity2 = new Patient();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setDateOfBirth(java.time.LocalDate.now().plusDays(1));
            entity2.setGender("anotherGender");
            entity2.setPhoneNumber("anotherPhoneNumber");
            entity2.setEmail("anotherEmail");
            entity2.setAddress("anotherAddress");
entityManager.persistAndFlush(entity2);

// When
List<Patient> entities = patientRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Patient entity = new Patient();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
            entity.setDateOfBirth(java.time.LocalDate.now().plusDays(i));
            entity.setGender("testGender" + i);
            entity.setPhoneNumber("testPhoneNumber" + i);
            entity.setEmail("testEmail" + i);
            entity.setAddress("testAddress" + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Patient> page = patientRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Patient savedEntity = entityManager.persistAndFlush(testPatient);

// When
boolean exists = patientRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = patientRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testPatient);

Patient entity2 = new Patient();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
            entity2.setDateOfBirth(java.time.LocalDate.now().plusDays(1));
            entity2.setGender("anotherGender");
            entity2.setPhoneNumber("anotherPhoneNumber");
            entity2.setEmail("anotherEmail");
            entity2.setAddress("anotherAddress");
entityManager.persistAndFlush(entity2);

// When
long count = patientRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}