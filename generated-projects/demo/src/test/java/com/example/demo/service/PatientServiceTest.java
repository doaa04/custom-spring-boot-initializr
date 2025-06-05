package com.example.demo.service;

import com.example.demo.dto.PatientDto;
import com.example.demo.entity.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Unit Tests")
class PatientServiceTest {

@Mock
private PatientRepository patientRepository;

@InjectMocks
private PatientServiceImpl patientService;

private Patient testPatient;
private PatientDto testPatientDto;

@BeforeEach
void setUp() {
testPatient = new Patient();
testPatient.setId(1L);
            testPatient.setFirstName("testFirstName");
            testPatient.setLastName("testLastName");
            testPatient.setDateOfBirth(java.time.LocalDate.now());
            testPatient.setGender("testGender");
            testPatient.setPhoneNumber("testPhoneNumber");
            testPatient.setEmail("testEmail");
            testPatient.setAddress("testAddress");

testPatientDto = new PatientDto();
testPatientDto.setId(1L);
            testPatientDto.setFirstName("testFirstName");
            testPatientDto.setLastName("testLastName");
            testPatientDto.setDateOfBirth(java.time.LocalDate.now());
            testPatientDto.setGender("testGender");
            testPatientDto.setPhoneNumber("testPhoneNumber");
            testPatientDto.setEmail("testEmail");
            testPatientDto.setAddress("testAddress");
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Patient> entities = Arrays.asList(testPatient);
when(patientRepository.findAll()).thenReturn(entities);

// When
List<PatientDto> result = patientService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testPatient.getId());
    verify(patientRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(patientRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<PatientDto> result = patientService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(patientRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));

        // When
        Optional<PatientDto> result = patientService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(patientRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<PatientDto> result = patientService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(patientRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

                // When
                PatientDto result = patientService.save(testPatientDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testPatient.getId());
                verify(patientRepository).save(any(Patient.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
                when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

                // When
                Optional<PatientDto> result = patientService.update(1L, testPatientDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(patientRepository).findById(1L);
                    verify(patientRepository).save(any(Patient.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<PatientDto> result = patientService.update(999L, testPatientDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(patientRepository).findById(999L);
                        verify(patientRepository, never()).save(any(Patient.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(patientRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = patientService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(patientRepository).existsById(1L);
                        verify(patientRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(patientRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = patientService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(patientRepository).existsById(999L);
                        verify(patientRepository, never()).deleteById(anyLong());
                        }
                        }