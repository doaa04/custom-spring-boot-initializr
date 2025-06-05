package com.example.demo.service;

import com.example.demo.dto.PrescriptionDto;
import com.example.demo.entity.Prescription;
import com.example.demo.repository.PrescriptionRepository;
import com.example.demo.service.impl.PrescriptionServiceImpl;
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
@DisplayName("Prescription Service Unit Tests")
class PrescriptionServiceTest {

@Mock
private PrescriptionRepository prescriptionRepository;

@InjectMocks
private PrescriptionServiceImpl prescriptionService;

private Prescription testPrescription;
private PrescriptionDto testPrescriptionDto;

@BeforeEach
void setUp() {
testPrescription = new Prescription();
testPrescription.setId(1L);
            testPrescription.setDateIssued(java.time.LocalDate.now());
            testPrescription.setMedicationDetails("testMedicationDetails");
            testPrescription.setDosageInstructions("testDosageInstructions");
            testPrescription.setPatientId(100L);
            testPrescription.setDoctorId(100L);

testPrescriptionDto = new PrescriptionDto();
testPrescriptionDto.setId(1L);
            testPrescriptionDto.setDateIssued(java.time.LocalDate.now());
            testPrescriptionDto.setMedicationDetails("testMedicationDetails");
            testPrescriptionDto.setDosageInstructions("testDosageInstructions");
            testPrescriptionDto.setPatientId(100L);
            testPrescriptionDto.setDoctorId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Prescription> entities = Arrays.asList(testPrescription);
when(prescriptionRepository.findAll()).thenReturn(entities);

// When
List<PrescriptionDto> result = prescriptionService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testPrescription.getId());
    verify(prescriptionRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(prescriptionRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<PrescriptionDto> result = prescriptionService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(prescriptionRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(prescriptionRepository.findById(anyLong())).thenReturn(Optional.of(testPrescription));

        // When
        Optional<PrescriptionDto> result = prescriptionService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(prescriptionRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(prescriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<PrescriptionDto> result = prescriptionService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(prescriptionRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(prescriptionRepository.save(any(Prescription.class))).thenReturn(testPrescription);

                // When
                PrescriptionDto result = prescriptionService.save(testPrescriptionDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testPrescription.getId());
                verify(prescriptionRepository).save(any(Prescription.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(prescriptionRepository.findById(anyLong())).thenReturn(Optional.of(testPrescription));
                when(prescriptionRepository.save(any(Prescription.class))).thenReturn(testPrescription);

                // When
                Optional<PrescriptionDto> result = prescriptionService.update(1L, testPrescriptionDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(prescriptionRepository).findById(1L);
                    verify(prescriptionRepository).save(any(Prescription.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(prescriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<PrescriptionDto> result = prescriptionService.update(999L, testPrescriptionDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(prescriptionRepository).findById(999L);
                        verify(prescriptionRepository, never()).save(any(Prescription.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(prescriptionRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = prescriptionService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(prescriptionRepository).existsById(1L);
                        verify(prescriptionRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(prescriptionRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = prescriptionService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(prescriptionRepository).existsById(999L);
                        verify(prescriptionRepository, never()).deleteById(anyLong());
                        }
                        }