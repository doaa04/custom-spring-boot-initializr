package com.example.demo.service;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.impl.DepartmentServiceImpl;
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
@DisplayName("Department Service Unit Tests")
class DepartmentServiceTest {

@Mock
private DepartmentRepository departmentRepository;

@InjectMocks
private DepartmentServiceImpl departmentService;

private Department testDepartment;
private DepartmentDto testDepartmentDto;

@BeforeEach
void setUp() {
testDepartment = new Department();
testDepartment.setId(1L);
            testDepartment.setName("testName");
            testDepartment.setLocation("testLocation");

testDepartmentDto = new DepartmentDto();
testDepartmentDto.setId(1L);
            testDepartmentDto.setName("testName");
            testDepartmentDto.setLocation("testLocation");
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Department> entities = Arrays.asList(testDepartment);
when(departmentRepository.findAll()).thenReturn(entities);

// When
List<DepartmentDto> result = departmentService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testDepartment.getId());
    verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<DepartmentDto> result = departmentService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(departmentRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(testDepartment));

        // When
        Optional<DepartmentDto> result = departmentService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(departmentRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<DepartmentDto> result = departmentService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(departmentRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

                // When
                DepartmentDto result = departmentService.save(testDepartmentDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testDepartment.getId());
                verify(departmentRepository).save(any(Department.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(testDepartment));
                when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

                // When
                Optional<DepartmentDto> result = departmentService.update(1L, testDepartmentDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(departmentRepository).findById(1L);
                    verify(departmentRepository).save(any(Department.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<DepartmentDto> result = departmentService.update(999L, testDepartmentDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(departmentRepository).findById(999L);
                        verify(departmentRepository, never()).save(any(Department.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(departmentRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = departmentService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(departmentRepository).existsById(1L);
                        verify(departmentRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(departmentRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = departmentService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(departmentRepository).existsById(999L);
                        verify(departmentRepository, never()).deleteById(anyLong());
                        }
                        }