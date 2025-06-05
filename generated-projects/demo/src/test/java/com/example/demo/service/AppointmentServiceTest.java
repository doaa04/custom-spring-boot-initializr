package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.entity.Appointment;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.impl.AppointmentServiceImpl;
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
@DisplayName("Appointment Service Unit Tests")
class AppointmentServiceTest {

@Mock
private AppointmentRepository appointmentRepository;

@InjectMocks
private AppointmentServiceImpl appointmentService;

private Appointment testAppointment;
private AppointmentDto testAppointmentDto;

@BeforeEach
void setUp() {
testAppointment = new Appointment();
testAppointment.setId(1L);
            testAppointment.setAppointmentDate(java.time.LocalDate.now());
            testAppointment.setAppointmentTime("testAppointmentTime");
            testAppointment.setReason("testReason");
            testAppointment.setStatus("testStatus");
            testAppointment.setPatientId(100L);
            testAppointment.setDoctorId(100L);

testAppointmentDto = new AppointmentDto();
testAppointmentDto.setId(1L);
            testAppointmentDto.setAppointmentDate(java.time.LocalDate.now());
            testAppointmentDto.setAppointmentTime("testAppointmentTime");
            testAppointmentDto.setReason("testReason");
            testAppointmentDto.setStatus("testStatus");
            testAppointmentDto.setPatientId(100L);
            testAppointmentDto.setDoctorId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Appointment> entities = Arrays.asList(testAppointment);
when(appointmentRepository.findAll()).thenReturn(entities);

// When
List<AppointmentDto> result = appointmentService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testAppointment.getId());
    verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<AppointmentDto> result = appointmentService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(appointmentRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));

        // When
        Optional<AppointmentDto> result = appointmentService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(appointmentRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<AppointmentDto> result = appointmentService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(appointmentRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

                // When
                AppointmentDto result = appointmentService.save(testAppointmentDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testAppointment.getId());
                verify(appointmentRepository).save(any(Appointment.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));
                when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

                // When
                Optional<AppointmentDto> result = appointmentService.update(1L, testAppointmentDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(appointmentRepository).findById(1L);
                    verify(appointmentRepository).save(any(Appointment.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<AppointmentDto> result = appointmentService.update(999L, testAppointmentDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(appointmentRepository).findById(999L);
                        verify(appointmentRepository, never()).save(any(Appointment.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(appointmentRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = appointmentService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(appointmentRepository).existsById(1L);
                        verify(appointmentRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(appointmentRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = appointmentService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(appointmentRepository).existsById(999L);
                        verify(appointmentRepository, never()).deleteById(anyLong());
                        }
                        }