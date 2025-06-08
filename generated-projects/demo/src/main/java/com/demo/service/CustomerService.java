package com.demo.service;

import com.demo.dto.CustomerDto;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
List<CustomerDto> findAll();
    Optional<CustomerDto> findById(Long id);
        CustomerDto save(CustomerDto customerDto);
        Optional<CustomerDto> update(Long id, CustomerDto customerDto);
            boolean deleteById(Long id);
            
    // AI-Generated Service Method Signatures
    public boolean existsByName(String name);
    public long countCustomersWithBudgetGreaterThan(int amount);
    
}