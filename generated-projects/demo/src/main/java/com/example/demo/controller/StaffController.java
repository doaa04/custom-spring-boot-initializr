package com.example.demo.controller;

import com.example.demo.dto.StaffDto;
import com.example.demo.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/staffs") // Simple pluralization
public class StaffController {

private final StaffService staffService;

public StaffController(StaffService staffService) {
this.staffService = staffService;
}

@GetMapping
public ResponseEntity<List<StaffDto>> getAllStaff() {
    return ResponseEntity.ok(staffService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable Long id) {
        return staffService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<StaffDto> createStaff(@Valid @RequestBody StaffDto staffDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (staffDto.getId() != null) {
            staffDto.setId(null);
            }
            StaffDto savedDto = staffService.save(staffDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<StaffDto> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDto staffDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (staffDto.getId() == null) {
                staffDto.setId(id);
                } else if (!staffDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return staffService.update(id, staffDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
                    if (staffService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with id " + id);
                    }
                    }
                    }