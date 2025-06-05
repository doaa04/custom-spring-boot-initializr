package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/departments") // Simple pluralization
public class DepartmentController {

private final DepartmentService departmentService;

public DepartmentController(DepartmentService departmentService) {
this.departmentService = departmentService;
}

@GetMapping
public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
    return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        return departmentService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (departmentDto.getId() != null) {
            departmentDto.setId(null);
            }
            DepartmentDto savedDto = departmentService.save(departmentDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDto departmentDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (departmentDto.getId() == null) {
                departmentDto.setId(id);
                } else if (!departmentDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return departmentService.update(id, departmentDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
                    if (departmentService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found with id " + id);
                    }
                    }
                    }