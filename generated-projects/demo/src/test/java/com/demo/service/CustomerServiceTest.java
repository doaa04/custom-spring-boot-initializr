package com.demo.service;

import com.demo.dto.CustomerDto;
import com.demo.entity.Customer;
import com.demo.repository.CustomerRepository;
import com.demo.service.impl.CustomerServiceImpl;
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
@DisplayName("Customer Service Unit Tests")
class CustomerServiceTest {

@Mock
private CustomerRepository customerRepository;

@InjectMocks
private CustomerServiceImpl customerService;

private Customer testCustomer;
private CustomerDto testCustomerDto;

@BeforeEach
void setUp() {
testCustomer = new Customer();
testCustomer.setId(1L);
            testCustomer.setName("testName");
            testCustomer.setBudget(100);

testCustomerDto = new CustomerDto();
testCustomerDto.setId(1L);
            testCustomerDto.setName("testName");
            testCustomerDto.setBudget(100);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Customer> entities = Arrays.asList(testCustomer);
when(customerRepository.findAll()).thenReturn(entities);

// When
List<CustomerDto> result = customerService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testCustomer.getId());
    verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(customerRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<CustomerDto> result = customerService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(customerRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        // When
        Optional<CustomerDto> result = customerService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(customerRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<CustomerDto> result = customerService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(customerRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

                // When
                CustomerDto result = customerService.save(testCustomerDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testCustomer.getId());
                verify(customerRepository).save(any(Customer.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
                when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

                // When
                Optional<CustomerDto> result = customerService.update(1L, testCustomerDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(customerRepository).findById(1L);
                    verify(customerRepository).save(any(Customer.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<CustomerDto> result = customerService.update(999L, testCustomerDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(customerRepository).findById(999L);
                        verify(customerRepository, never()).save(any(Customer.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(customerRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = customerService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(customerRepository).existsById(1L);
                        verify(customerRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(customerRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = customerService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(customerRepository).existsById(999L);
                        verify(customerRepository, never()).deleteById(anyLong());
                        }
                        }