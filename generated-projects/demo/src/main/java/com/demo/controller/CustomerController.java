package com.demo.controller;

import com.demo.dto.CustomerDto;
import com.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers") // Simple pluralization
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id " + id));
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        // Ensure ID is null for creation if it's passed in DTO
        if (customerDto.getId() != null) {
            customerDto.setId(null);
        }
        CustomerDto savedDto = customerService.save(customerDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto) {
        // Ensure the DTO's ID matches the path variable ID, or set it.
        if (customerDto.getId() == null) {
            customerDto.setId(id);
        } else if (!customerDto.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
        }

        return customerService.update(id, customerDto)
                .map(ResponseEntity::ok)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id " + id);
        }
    }

    // AI-Generated Controller Endpoints
    @GetMapping("/existsbyname")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        boolean result = customerService.existsByName(name);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/countcustomerswithbudgetgreaterthan")
    public ResponseEntity<Long> countCustomersWithBudgetGreaterThan(@RequestParam int amount) {
        long result = customerService.countCustomersWithBudgetGreaterThan(amount);
        return ResponseEntity.ok(result);
    }

}