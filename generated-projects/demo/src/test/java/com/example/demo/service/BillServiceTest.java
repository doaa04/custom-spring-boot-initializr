package com.example.demo.service;

import com.example.demo.dto.BillDto;
import com.example.demo.entity.Bill;
import com.example.demo.repository.BillRepository;
import com.example.demo.service.impl.BillServiceImpl;
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
@DisplayName("Bill Service Unit Tests")
class BillServiceTest {

@Mock
private BillRepository billRepository;

@InjectMocks
private BillServiceImpl billService;

private Bill testBill;
private BillDto testBillDto;

@BeforeEach
void setUp() {
testBill = new Bill();
testBill.setId(1L);
            testBill.setAmount(100.0);
            testBill.setIssueDate(java.time.LocalDate.now());
            testBill.setDueDate(java.time.LocalDate.now());
            testBill.setStatus("testStatus");
            testBill.setPatientId(100L);

testBillDto = new BillDto();
testBillDto.setId(1L);
            testBillDto.setAmount(100.0);
            testBillDto.setIssueDate(java.time.LocalDate.now());
            testBillDto.setDueDate(java.time.LocalDate.now());
            testBillDto.setStatus("testStatus");
            testBillDto.setPatientId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Bill> entities = Arrays.asList(testBill);
when(billRepository.findAll()).thenReturn(entities);

// When
List<BillDto> result = billService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testBill.getId());
    verify(billRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(billRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<BillDto> result = billService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(billRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(billRepository.findById(anyLong())).thenReturn(Optional.of(testBill));

        // When
        Optional<BillDto> result = billService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(billRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(billRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<BillDto> result = billService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(billRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(billRepository.save(any(Bill.class))).thenReturn(testBill);

                // When
                BillDto result = billService.save(testBillDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testBill.getId());
                verify(billRepository).save(any(Bill.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(billRepository.findById(anyLong())).thenReturn(Optional.of(testBill));
                when(billRepository.save(any(Bill.class))).thenReturn(testBill);

                // When
                Optional<BillDto> result = billService.update(1L, testBillDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(billRepository).findById(1L);
                    verify(billRepository).save(any(Bill.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(billRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<BillDto> result = billService.update(999L, testBillDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(billRepository).findById(999L);
                        verify(billRepository, never()).save(any(Bill.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(billRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = billService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(billRepository).existsById(1L);
                        verify(billRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(billRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = billService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(billRepository).existsById(999L);
                        verify(billRepository, never()).deleteById(anyLong());
                        }
                        }