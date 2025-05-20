package com.example.demo.controller;

import com.example.demo.dto.SchoolDto;
import com.example.demo.service.SchoolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/schools") // Simple pluralization
public class SchoolController {

private final SchoolService schoolService;

public SchoolController(SchoolService schoolService) {
this.schoolService = schoolService;
}

@GetMapping
public ResponseEntity<List<SchoolDto>> getAllSchools() {
    return ResponseEntity.ok(schoolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDto> getSchoolById(@PathVariable Long id) {
        return schoolService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<SchoolDto> createSchool(@Valid @RequestBody SchoolDto schoolDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (schoolDto.getId() != null) {
            schoolDto.setId(null);
            }
            SchoolDto savedDto = schoolService.save(schoolDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<SchoolDto> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDto schoolDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (schoolDto.getId() == null) {
                schoolDto.setId(id);
                } else if (!schoolDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return schoolService.update(id, schoolDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
                    if (schoolService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found with id " + id);
                    }
                    }
                    }