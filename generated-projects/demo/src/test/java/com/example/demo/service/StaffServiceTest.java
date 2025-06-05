package com.example.demo.service;

import com.example.demo.dto.StaffDto;
import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.impl.StaffServiceImpl;
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
@DisplayName("Staff Service Unit Tests")
class StaffServiceTest {

@Mock
private StaffRepository staffRepository;

@InjectMocks
private StaffServiceImpl staffService;

private Staff testStaff;
private StaffDto testStaffDto;

@BeforeEach
void setUp() {
testStaff = new Staff();
testStaff.setId(1L);
            testStaff.setFirstName("testFirstName");
            testStaff.setLastName("testLastName");
            testStaff.setRole("testRole");
            testStaff.setEmail("testEmail");
            testStaff.setPhoneNumber("testPhoneNumber");
            testStaff.setDepartmentId(100L);

testStaffDto = new StaffDto();
testStaffDto.setId(1L);
            testStaffDto.setFirstName("testFirstName");
            testStaffDto.setLastName("testLastName");
            testStaffDto.setRole("testRole");
            testStaffDto.setEmail("testEmail");
            testStaffDto.setPhoneNumber("testPhoneNumber");
            testStaffDto.setDepartmentId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Staff> entities = Arrays.asList(testStaff);
when(staffRepository.findAll()).thenReturn(entities);

// When
List<StaffDto> result = staffService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testStaff.getId());
    verify(staffRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(staffRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<StaffDto> result = staffService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(staffRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(staffRepository.findById(anyLong())).thenReturn(Optional.of(testStaff));

        // When
        Optional<StaffDto> result = staffService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(staffRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(staffRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<StaffDto> result = staffService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(staffRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

                // When
                StaffDto result = staffService.save(testStaffDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testStaff.getId());
                verify(staffRepository).save(any(Staff.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(staffRepository.findById(anyLong())).thenReturn(Optional.of(testStaff));
                when(staffRepository.save(any(Staff.class))).thenReturn(testStaff);

                // When
                Optional<StaffDto> result = staffService.update(1L, testStaffDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(staffRepository).findById(1L);
                    verify(staffRepository).save(any(Staff.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(staffRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<StaffDto> result = staffService.update(999L, testStaffDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(staffRepository).findById(999L);
                        verify(staffRepository, never()).save(any(Staff.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(staffRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = staffService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(staffRepository).existsById(1L);
                        verify(staffRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(staffRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = staffService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(staffRepository).existsById(999L);
                        verify(staffRepository, never()).deleteById(anyLong());
                        }
                        }