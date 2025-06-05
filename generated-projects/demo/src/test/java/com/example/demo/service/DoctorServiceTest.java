package com.example.demo.service;

import com.example.demo.dto.DoctorDto;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.service.impl.DoctorServiceImpl;
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
@DisplayName("Doctor Service Unit Tests")
class DoctorServiceTest {

@Mock
private DoctorRepository doctorRepository;

@InjectMocks
private DoctorServiceImpl doctorService;

private Doctor testDoctor;
private DoctorDto testDoctorDto;

@BeforeEach
void setUp() {
testDoctor = new Doctor();
testDoctor.setId(1L);
            testDoctor.setFirstName("testFirstName");
            testDoctor.setLastName("testLastName");
            testDoctor.setSpecialization("testSpecialization");
            testDoctor.setEmail("testEmail");
            testDoctor.setPhoneNumber("testPhoneNumber");

testDoctorDto = new DoctorDto();
testDoctorDto.setId(1L);
            testDoctorDto.setFirstName("testFirstName");
            testDoctorDto.setLastName("testLastName");
            testDoctorDto.setSpecialization("testSpecialization");
            testDoctorDto.setEmail("testEmail");
            testDoctorDto.setPhoneNumber("testPhoneNumber");
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Doctor> entities = Arrays.asList(testDoctor);
when(doctorRepository.findAll()).thenReturn(entities);

// When
List<DoctorDto> result = doctorService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testDoctor.getId());
    verify(doctorRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<DoctorDto> result = doctorService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(doctorRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(testDoctor));

        // When
        Optional<DoctorDto> result = doctorService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(doctorRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<DoctorDto> result = doctorService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(doctorRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

                // When
                DoctorDto result = doctorService.save(testDoctorDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testDoctor.getId());
                verify(doctorRepository).save(any(Doctor.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(testDoctor));
                when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

                // When
                Optional<DoctorDto> result = doctorService.update(1L, testDoctorDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(doctorRepository).findById(1L);
                    verify(doctorRepository).save(any(Doctor.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<DoctorDto> result = doctorService.update(999L, testDoctorDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(doctorRepository).findById(999L);
                        verify(doctorRepository, never()).save(any(Doctor.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(doctorRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = doctorService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(doctorRepository).existsById(1L);
                        verify(doctorRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(doctorRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = doctorService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(doctorRepository).existsById(999L);
                        verify(doctorRepository, never()).deleteById(anyLong());
                        }
                        }