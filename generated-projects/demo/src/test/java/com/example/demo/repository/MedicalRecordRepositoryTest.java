package com.example.demo.repository;

import com.example.demo.entity.MedicalRecord;
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
@DisplayName("MedicalRecord Repository Tests")
class MedicalRecordRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private MedicalRecordRepository medicalrecordRepository;

private MedicalRecord testMedicalRecord;

@BeforeEach
void setUp() {
testMedicalRecord = new MedicalRecord();
            testMedicalRecord.setRecordDate(java.time.LocalDate.now());
            testMedicalRecord.setDescription("testDescription");
            testMedicalRecord.setDiagnosis("testDiagnosis");
            testMedicalRecord.setTreatment("testTreatment");
            testMedicalRecord.setPatientId(100L);
            testMedicalRecord.setDoctorId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
MedicalRecord savedEntity = entityManager.persistAndFlush(testMedicalRecord);

// When
Optional<MedicalRecord> result = medicalrecordRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getRecordDate()).isEqualTo(testMedicalRecord.getRecordDate());
        assertThat(result.get().getDescription()).isEqualTo(testMedicalRecord.getDescription());
        assertThat(result.get().getDiagnosis()).isEqualTo(testMedicalRecord.getDiagnosis());
        assertThat(result.get().getTreatment()).isEqualTo(testMedicalRecord.getTreatment());
        assertThat(result.get().getPatientId()).isEqualTo(testMedicalRecord.getPatientId());
        assertThat(result.get().getDoctorId()).isEqualTo(testMedicalRecord.getDoctorId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<MedicalRecord> result = medicalrecordRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
MedicalRecord savedEntity = medicalrecordRepository.save(testMedicalRecord);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getRecordDate()).isEqualTo(testMedicalRecord.getRecordDate());
        assertThat(savedEntity.getDescription()).isEqualTo(testMedicalRecord.getDescription());
        assertThat(savedEntity.getDiagnosis()).isEqualTo(testMedicalRecord.getDiagnosis());
        assertThat(savedEntity.getTreatment()).isEqualTo(testMedicalRecord.getTreatment());
        assertThat(savedEntity.getPatientId()).isEqualTo(testMedicalRecord.getPatientId());
        assertThat(savedEntity.getDoctorId()).isEqualTo(testMedicalRecord.getDoctorId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
MedicalRecord savedEntity = entityManager.persistAndFlush(testMedicalRecord);
entityManager.detach(savedEntity);

            savedEntity.setRecordDate(java.time.LocalDate.now().plusDays(1));
            savedEntity.setDescription("updatedDescription");
            savedEntity.setDiagnosis("updatedDiagnosis");
            savedEntity.setTreatment("updatedTreatment");
            savedEntity.setPatientId(200L);
            savedEntity.setDoctorId(200L);

// When
MedicalRecord updatedEntity = medicalrecordRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getRecordDate()).isEqualTo(savedEntity.getRecordDate());
        assertThat(updatedEntity.getDescription()).isEqualTo(savedEntity.getDescription());
        assertThat(updatedEntity.getDiagnosis()).isEqualTo(savedEntity.getDiagnosis());
        assertThat(updatedEntity.getTreatment()).isEqualTo(savedEntity.getTreatment());
        assertThat(updatedEntity.getPatientId()).isEqualTo(savedEntity.getPatientId());
        assertThat(updatedEntity.getDoctorId()).isEqualTo(savedEntity.getDoctorId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
MedicalRecord savedEntity = entityManager.persistAndFlush(testMedicalRecord);

// When
medicalrecordRepository.deleteById(savedEntity.getId());

// Then
Optional<MedicalRecord> result = medicalrecordRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
MedicalRecord entity1 = entityManager.persistAndFlush(testMedicalRecord);

MedicalRecord entity2 = new MedicalRecord();
            entity2.setRecordDate(java.time.LocalDate.now().plusDays(1));
            entity2.setDescription("anotherDescription");
            entity2.setDiagnosis("anotherDiagnosis");
            entity2.setTreatment("anotherTreatment");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
List<MedicalRecord> entities = medicalrecordRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
MedicalRecord entity = new MedicalRecord();
            entity.setRecordDate(java.time.LocalDate.now().plusDays(i));
            entity.setDescription("testDescription" + i);
            entity.setDiagnosis("testDiagnosis" + i);
            entity.setTreatment("testTreatment" + i);
            entity.setPatientId(100L + i);
            entity.setDoctorId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<MedicalRecord> page = medicalrecordRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
MedicalRecord savedEntity = entityManager.persistAndFlush(testMedicalRecord);

// When
boolean exists = medicalrecordRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = medicalrecordRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testMedicalRecord);

MedicalRecord entity2 = new MedicalRecord();
            entity2.setRecordDate(java.time.LocalDate.now().plusDays(1));
            entity2.setDescription("anotherDescription");
            entity2.setDiagnosis("anotherDiagnosis");
            entity2.setTreatment("anotherTreatment");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = medicalrecordRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}