package com.example.demo.service;

import com.example.demo.dto.MedicalRecordDto;
import com.example.demo.entity.MedicalRecord;
import com.example.demo.repository.MedicalRecordRepository;
import com.example.demo.service.impl.MedicalRecordServiceImpl;
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
@DisplayName("MedicalRecord Service Unit Tests")
class MedicalRecordServiceTest {

@Mock
private MedicalRecordRepository medicalrecordRepository;

@InjectMocks
private MedicalRecordServiceImpl medicalrecordService;

private MedicalRecord testMedicalRecord;
private MedicalRecordDto testMedicalRecordDto;

@BeforeEach
void setUp() {
testMedicalRecord = new MedicalRecord();
testMedicalRecord.setId(1L);
            testMedicalRecord.setRecordDate(java.time.LocalDate.now());
            testMedicalRecord.setDescription("testDescription");
            testMedicalRecord.setDiagnosis("testDiagnosis");
            testMedicalRecord.setTreatment("testTreatment");
            testMedicalRecord.setPatientId(100L);
            testMedicalRecord.setDoctorId(100L);

testMedicalRecordDto = new MedicalRecordDto();
testMedicalRecordDto.setId(1L);
            testMedicalRecordDto.setRecordDate(java.time.LocalDate.now());
            testMedicalRecordDto.setDescription("testDescription");
            testMedicalRecordDto.setDiagnosis("testDiagnosis");
            testMedicalRecordDto.setTreatment("testTreatment");
            testMedicalRecordDto.setPatientId(100L);
            testMedicalRecordDto.setDoctorId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<MedicalRecord> entities = Arrays.asList(testMedicalRecord);
when(medicalrecordRepository.findAll()).thenReturn(entities);

// When
List<MedicalRecordDto> result = medicalrecordService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testMedicalRecord.getId());
    verify(medicalrecordRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(medicalrecordRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<MedicalRecordDto> result = medicalrecordService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(medicalrecordRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(medicalrecordRepository.findById(anyLong())).thenReturn(Optional.of(testMedicalRecord));

        // When
        Optional<MedicalRecordDto> result = medicalrecordService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(medicalrecordRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(medicalrecordRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<MedicalRecordDto> result = medicalrecordService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(medicalrecordRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(medicalrecordRepository.save(any(MedicalRecord.class))).thenReturn(testMedicalRecord);

                // When
                MedicalRecordDto result = medicalrecordService.save(testMedicalRecordDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testMedicalRecord.getId());
                verify(medicalrecordRepository).save(any(MedicalRecord.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(medicalrecordRepository.findById(anyLong())).thenReturn(Optional.of(testMedicalRecord));
                when(medicalrecordRepository.save(any(MedicalRecord.class))).thenReturn(testMedicalRecord);

                // When
                Optional<MedicalRecordDto> result = medicalrecordService.update(1L, testMedicalRecordDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(medicalrecordRepository).findById(1L);
                    verify(medicalrecordRepository).save(any(MedicalRecord.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(medicalrecordRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<MedicalRecordDto> result = medicalrecordService.update(999L, testMedicalRecordDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(medicalrecordRepository).findById(999L);
                        verify(medicalrecordRepository, never()).save(any(MedicalRecord.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(medicalrecordRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = medicalrecordService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(medicalrecordRepository).existsById(1L);
                        verify(medicalrecordRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(medicalrecordRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = medicalrecordService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(medicalrecordRepository).existsById(999L);
                        verify(medicalrecordRepository, never()).deleteById(anyLong());
                        }
                        }