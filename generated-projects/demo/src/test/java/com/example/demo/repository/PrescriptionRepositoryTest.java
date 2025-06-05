package com.example.demo.repository;

import com.example.demo.entity.Prescription;
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
@DisplayName("Prescription Repository Tests")
class PrescriptionRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private PrescriptionRepository prescriptionRepository;

private Prescription testPrescription;

@BeforeEach
void setUp() {
testPrescription = new Prescription();
            testPrescription.setDateIssued(java.time.LocalDate.now());
            testPrescription.setMedicationDetails("testMedicationDetails");
            testPrescription.setDosageInstructions("testDosageInstructions");
            testPrescription.setPatientId(100L);
            testPrescription.setDoctorId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Prescription savedEntity = entityManager.persistAndFlush(testPrescription);

// When
Optional<Prescription> result = prescriptionRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getDateIssued()).isEqualTo(testPrescription.getDateIssued());
        assertThat(result.get().getMedicationDetails()).isEqualTo(testPrescription.getMedicationDetails());
        assertThat(result.get().getDosageInstructions()).isEqualTo(testPrescription.getDosageInstructions());
        assertThat(result.get().getPatientId()).isEqualTo(testPrescription.getPatientId());
        assertThat(result.get().getDoctorId()).isEqualTo(testPrescription.getDoctorId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Prescription> result = prescriptionRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Prescription savedEntity = prescriptionRepository.save(testPrescription);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getDateIssued()).isEqualTo(testPrescription.getDateIssued());
        assertThat(savedEntity.getMedicationDetails()).isEqualTo(testPrescription.getMedicationDetails());
        assertThat(savedEntity.getDosageInstructions()).isEqualTo(testPrescription.getDosageInstructions());
        assertThat(savedEntity.getPatientId()).isEqualTo(testPrescription.getPatientId());
        assertThat(savedEntity.getDoctorId()).isEqualTo(testPrescription.getDoctorId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Prescription savedEntity = entityManager.persistAndFlush(testPrescription);
entityManager.detach(savedEntity);

            savedEntity.setDateIssued(java.time.LocalDate.now().plusDays(1));
            savedEntity.setMedicationDetails("updatedMedicationDetails");
            savedEntity.setDosageInstructions("updatedDosageInstructions");
            savedEntity.setPatientId(200L);
            savedEntity.setDoctorId(200L);

// When
Prescription updatedEntity = prescriptionRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getDateIssued()).isEqualTo(savedEntity.getDateIssued());
        assertThat(updatedEntity.getMedicationDetails()).isEqualTo(savedEntity.getMedicationDetails());
        assertThat(updatedEntity.getDosageInstructions()).isEqualTo(savedEntity.getDosageInstructions());
        assertThat(updatedEntity.getPatientId()).isEqualTo(savedEntity.getPatientId());
        assertThat(updatedEntity.getDoctorId()).isEqualTo(savedEntity.getDoctorId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Prescription savedEntity = entityManager.persistAndFlush(testPrescription);

// When
prescriptionRepository.deleteById(savedEntity.getId());

// Then
Optional<Prescription> result = prescriptionRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Prescription entity1 = entityManager.persistAndFlush(testPrescription);

Prescription entity2 = new Prescription();
            entity2.setDateIssued(java.time.LocalDate.now().plusDays(1));
            entity2.setMedicationDetails("anotherMedicationDetails");
            entity2.setDosageInstructions("anotherDosageInstructions");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Prescription> entities = prescriptionRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Prescription entity = new Prescription();
            entity.setDateIssued(java.time.LocalDate.now().plusDays(i));
            entity.setMedicationDetails("testMedicationDetails" + i);
            entity.setDosageInstructions("testDosageInstructions" + i);
            entity.setPatientId(100L + i);
            entity.setDoctorId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Prescription> page = prescriptionRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Prescription savedEntity = entityManager.persistAndFlush(testPrescription);

// When
boolean exists = prescriptionRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = prescriptionRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testPrescription);

Prescription entity2 = new Prescription();
            entity2.setDateIssued(java.time.LocalDate.now().plusDays(1));
            entity2.setMedicationDetails("anotherMedicationDetails");
            entity2.setDosageInstructions("anotherDosageInstructions");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = prescriptionRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}