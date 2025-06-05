package com.example.demo.repository;

import com.example.demo.entity.Appointment;
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
@DisplayName("Appointment Repository Tests")
class AppointmentRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private AppointmentRepository appointmentRepository;

private Appointment testAppointment;

@BeforeEach
void setUp() {
testAppointment = new Appointment();
            testAppointment.setAppointmentDate(java.time.LocalDate.now());
            testAppointment.setAppointmentTime("testAppointmentTime");
            testAppointment.setReason("testReason");
            testAppointment.setStatus("testStatus");
            testAppointment.setPatientId(100L);
            testAppointment.setDoctorId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Appointment savedEntity = entityManager.persistAndFlush(testAppointment);

// When
Optional<Appointment> result = appointmentRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getAppointmentDate()).isEqualTo(testAppointment.getAppointmentDate());
        assertThat(result.get().getAppointmentTime()).isEqualTo(testAppointment.getAppointmentTime());
        assertThat(result.get().getReason()).isEqualTo(testAppointment.getReason());
        assertThat(result.get().getStatus()).isEqualTo(testAppointment.getStatus());
        assertThat(result.get().getPatientId()).isEqualTo(testAppointment.getPatientId());
        assertThat(result.get().getDoctorId()).isEqualTo(testAppointment.getDoctorId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Appointment> result = appointmentRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Appointment savedEntity = appointmentRepository.save(testAppointment);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getAppointmentDate()).isEqualTo(testAppointment.getAppointmentDate());
        assertThat(savedEntity.getAppointmentTime()).isEqualTo(testAppointment.getAppointmentTime());
        assertThat(savedEntity.getReason()).isEqualTo(testAppointment.getReason());
        assertThat(savedEntity.getStatus()).isEqualTo(testAppointment.getStatus());
        assertThat(savedEntity.getPatientId()).isEqualTo(testAppointment.getPatientId());
        assertThat(savedEntity.getDoctorId()).isEqualTo(testAppointment.getDoctorId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Appointment savedEntity = entityManager.persistAndFlush(testAppointment);
entityManager.detach(savedEntity);

            savedEntity.setAppointmentDate(java.time.LocalDate.now().plusDays(1));
            savedEntity.setAppointmentTime("updatedAppointmentTime");
            savedEntity.setReason("updatedReason");
            savedEntity.setStatus("updatedStatus");
            savedEntity.setPatientId(200L);
            savedEntity.setDoctorId(200L);

// When
Appointment updatedEntity = appointmentRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getAppointmentDate()).isEqualTo(savedEntity.getAppointmentDate());
        assertThat(updatedEntity.getAppointmentTime()).isEqualTo(savedEntity.getAppointmentTime());
        assertThat(updatedEntity.getReason()).isEqualTo(savedEntity.getReason());
        assertThat(updatedEntity.getStatus()).isEqualTo(savedEntity.getStatus());
        assertThat(updatedEntity.getPatientId()).isEqualTo(savedEntity.getPatientId());
        assertThat(updatedEntity.getDoctorId()).isEqualTo(savedEntity.getDoctorId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Appointment savedEntity = entityManager.persistAndFlush(testAppointment);

// When
appointmentRepository.deleteById(savedEntity.getId());

// Then
Optional<Appointment> result = appointmentRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Appointment entity1 = entityManager.persistAndFlush(testAppointment);

Appointment entity2 = new Appointment();
            entity2.setAppointmentDate(java.time.LocalDate.now().plusDays(1));
            entity2.setAppointmentTime("anotherAppointmentTime");
            entity2.setReason("anotherReason");
            entity2.setStatus("anotherStatus");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Appointment> entities = appointmentRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Appointment entity = new Appointment();
            entity.setAppointmentDate(java.time.LocalDate.now().plusDays(i));
            entity.setAppointmentTime("testAppointmentTime" + i);
            entity.setReason("testReason" + i);
            entity.setStatus("testStatus" + i);
            entity.setPatientId(100L + i);
            entity.setDoctorId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Appointment> page = appointmentRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Appointment savedEntity = entityManager.persistAndFlush(testAppointment);

// When
boolean exists = appointmentRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = appointmentRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testAppointment);

Appointment entity2 = new Appointment();
            entity2.setAppointmentDate(java.time.LocalDate.now().plusDays(1));
            entity2.setAppointmentTime("anotherAppointmentTime");
            entity2.setReason("anotherReason");
            entity2.setStatus("anotherStatus");
            entity2.setPatientId(200L);
            entity2.setDoctorId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = appointmentRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}