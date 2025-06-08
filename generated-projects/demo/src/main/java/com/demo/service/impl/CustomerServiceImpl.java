package com.demo.service.impl;

import com.demo.dto.CustomerDto;
import com.demo.entity.Customer;
import com.demo.repository.CustomerRepository;
import com.demo.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class CustomerServiceImpl implements CustomerService {

private final CustomerRepository customerRepository;

public CustomerServiceImpl(CustomerRepository customerRepository) {
this.customerRepository = customerRepository;
}

@Override
@Transactional(readOnly = true)
public List<CustomerDto> findAll() {
    return customerRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDto> findById(Long id) {
        return customerRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public CustomerDto save(CustomerDto customerDto) {
        Customer customer = convertToEntity(customerDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (customerDto.getId() == null) {
        customer.setId(null); // Ensure it's null for auto-generation
        }
        customer = customerRepository.save(customer);
        return convertToDto(customer);
        }

        @Override
        public Optional<CustomerDto> update(Long id, CustomerDto customerDto) {
            return customerRepository.findById(id)
            .map(existingCustomer -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (customerDto.getName() != null) { // Simple null check
                    existingCustomer.setName(customerDto.getName());
                    }
                    if (customerDto.getBudget() != null) { // Simple null check
                    existingCustomer.setBudget(customerDto.getBudget());
                    }
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return convertToDto(updatedCustomer);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private CustomerDto convertToDto(Customer customer) {
            CustomerDto dto = new CustomerDto();
            BeanUtils.copyProperties(customer, dto);
            return dto;
            }

            private Customer convertToEntity(CustomerDto customerDto) {
            Customer entity = new Customer();
            BeanUtils.copyProperties(customerDto, entity);
            return entity;
            }
            
    // AI-Generated Service Methods
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return customerRepository.findAll().stream()
            .anyMatch(customer -> customer.getName().equalsIgnoreCase(name));
    }
    
    @Transactional(readOnly = true)
    public long countCustomersWithBudgetGreaterThan(int amount) {
        return customerRepository.findAll().stream()
            .filter(customer -> customer.getBudget() != null && customer.getBudget() > amount)
            .count();
    }
}